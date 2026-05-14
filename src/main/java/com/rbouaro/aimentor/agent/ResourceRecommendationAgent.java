package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;

@Agent(description = "Recommend learning resources for a specific roadmap milestone")
public class ResourceRecommendationAgent {

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

                Recommend 3-5 specific learning resources that would help the user complete this milestone.
                For each resource, provide:
                1. A descriptive title
                2. A brief explanation of why it's valuable
                3. What type of resource it is (video, article, course, project, book, etc.)
                4. Keywords that could be used to search for this resource online

                Format your response in JSON with the following structure:
                {
                  "resources": [
                    {
                      "title": "Resource title",
                      "description": "Why this resource is valuable",
                      "type": "VIDEO|ARTICLE|COURSE|GITHUB_PROJECT|BOOK|OTHER",
                      "searchKeywords": ["keyword1", "keyword2", "keyword3"]
                    }
                  ]
                }
                IMPORTANT:
                  1. Return ONLY raw valid JSON - no additional text, comments, or explanations
                  2. Ensure all brackets and braces are properly closed
                  3. Do not use markdown code blocks
                  4. Make sure the JSON is complete and well-formed
                """.formatted(userInput.getContent());

        return new ResourceRecommendations(
                context.ai()
                        .withDefaultLlm()
                        .withPromptContributor(TUTOR)
                        .generateText(prompt)
        );
    }
}