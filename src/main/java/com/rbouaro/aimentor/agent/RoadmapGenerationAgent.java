package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;

@Agent(description = "Generate a structured JSON learning roadmap with milestones for a user's goal")
public class RoadmapGenerationAgent {

    static final RoleGoalBackstory TUTOR = new RoleGoalBackstory(
            "AI Learning Mentor",
            "Help users create personalized learning roadmaps with milestones and resources",
            "Experienced educator with deep knowledge across technology and professional skills"
    );

    public record RoadmapJson(String content) {}

    @AchievesGoal(description = "A structured learning roadmap in JSON format has been generated")
    @Action
    RoadmapJson generate(UserInput userInput, OperationContext context) {
        String prompt = """
                %s

                Create a detailed learning roadmap for the goal and context above.

                The roadmap should include:
                1. A title for the overall learning journey
                2. A brief plain-text description of the learning path (2-3 sentences)
                3. Sequential milestones that cover all areas of the topic, each with:
                   - A clear, specific title
                   - A short plain-text description of what the milestone covers (2-3 sentences)
                   - Estimated time to complete in hours

                Format your response as JSON:
                {
                  "title": "Roadmap title",
                  "description": "Brief description of the learning path",
                  "milestones": [
                    {
                      "title": "Milestone title",
                      "description": "Short description of what this milestone covers",
                      "estimatedTimeInHours": 10
                    }
                  ]
                }

                IMPORTANT: Return ONLY the raw JSON without any additional text, comments, or explanations.
                Do not wrap the JSON in markdown code blocks or add any prefixes/suffixes.
                """.formatted(userInput.getContent());

        return new RoadmapJson(
                context.ai()
                        .withDefaultLlm()
                        .withPromptContributor(TUTOR)
                        .generateText(prompt)
        );
    }
}