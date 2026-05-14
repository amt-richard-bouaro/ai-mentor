package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;

@Agent(description = "Generate clarifying questions to understand a user's learning goal")
public class ClarifyingQuestionsAgent {

    static final RoleGoalBackstory TUTOR = new RoleGoalBackstory(
            "AI Learning Mentor",
            "Help users create personalized learning roadmaps with milestones and resources",
            "Experienced educator with deep knowledge across technology and professional skills"
    );

    public record ClarifyingQuestions(String content) {}

    @AchievesGoal(description = "Clarifying questions generated for the user's learning goal")
    @Action
    ClarifyingQuestions generate(UserInput userInput, OperationContext context) {
        String prompt = """
                The user has expressed interest in learning: "%s".

                Generate 3-5 clarifying questions to better understand their:
                1. Current skill level and background
                2. Specific areas of interest within the broader topic
                3. Time commitment and learning pace
                4. Preferred learning style (hands-on projects, theory, etc.)
                5. End goal or what they want to achieve

                Format your response as a numbered list of questions only.
                """.formatted(userInput.getContent());

        return new ClarifyingQuestions(
                context.ai()
                        .withDefaultLlm()
                        .withPromptContributor(TUTOR)
                        .generateText(prompt)
        );
    }
}