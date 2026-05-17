package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.core.ToolGroup;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.rbouaro.aimentor.service.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Agent(description = "Recommend learning resources for a specific roadmap milestone")
@RequiredArgsConstructor
public class ResourceRecommendationAgent {

    private final ToolService toolService;

    @Autowired(required = false)
    @Qualifier("webToolGroup")
    private ToolGroup webToolGroup;

    static final RoleGoalBackstory TUTOR = new RoleGoalBackstory(
            "AI Learning Mentor",
            "Help users create personalized learning roadmaps with milestones and resources",
            "Experienced educator with deep knowledge across technology and professional skills"
    );

    public record ResourceRecommendations(String content) {}

    @AchievesGoal(description = "Learning resource recommendations in JSON format have been generated for the milestone")
    @Action
    ResourceRecommendations generate(UserInput userInput, OperationContext context) {
        String prompt = """
                %s

                Use the available search tools to find real, specific learning resources for this milestone:
                - Call searchYouTubeVideos to find tutorial videos (search for the milestone topic, max 3 results)
                - Use web search to find articles, official documentation, courses, and GitHub projects

                Recommend 3-5 specific, real learning resources based on what the tools return.
                For each resource provide:
                1. A descriptive title (the actual name of the resource found)
                2. A brief explanation of why it's valuable for this milestone
                3. The resource type: VIDEO, ARTICLE, COURSE, GITHUB_PROJECT, BOOK, or OTHER
                4. The exact URL returned by the search tool — do not fabricate URLs

                Format your response as JSON:
                {
                  "resources": [
                    {
                      "title": "Resource title",
                      "description": "Why this resource is valuable",
                      "type": "VIDEO|ARTICLE|COURSE|GITHUB_PROJECT|BOOK|OTHER",
                      "url": "https://actual-url-from-search.com"
                    }
                  ]
                }
                IMPORTANT:
                  1. Return ONLY raw valid JSON — no extra text, comments, or markdown code blocks
                  2. Ensure all brackets and braces are properly closed
                  3. Every resource must have a real URL from the search results starting with https://
                  4. Do not fabricate or guess URLs
                """.formatted(userInput.getContent());

        log.info("[RESOURCE_AGENT] Generating resources, webToolGroup available: {}", webToolGroup != null);

        var ai = context.ai()
                .withDefaultLlm()
                .withPromptContributor(TUTOR)
                .withToolObject(toolService);

        if (webToolGroup != null) {
            ai = ai.withToolGroup(webToolGroup);
        }

        return new ResourceRecommendations(ai.generateText(prompt));
    }
}