package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;

@Agent(description = "Generate multiple-choice quiz questions for a learning milestone or roadmap")
public class QuizGenerationAgent {

    static final RoleGoalBackstory TUTOR = new RoleGoalBackstory(
            "AI Learning Mentor",
            "Assess learner understanding through well-crafted multiple-choice questions",
            "Experienced educator skilled at writing clear, unambiguous assessment questions"
    );

    public record QuizJson(String content) {}

    @AchievesGoal(description = "Multiple-choice quiz questions have been generated in JSON format")
    @Action
    QuizJson generate(UserInput userInput, OperationContext context) {
        String prompt = """
                %s

                Generate multiple-choice questions to test understanding of the above topic.
                Each question must have exactly 4 options with only one correct answer.

                Return ONLY a valid JSON object — no markdown, no extra text:
                {
                  "questions": [
                    {
                      "question": "Clear, specific question text?",
                      "options": ["Option A", "Option B", "Option C", "Option D"],
                      "correctIndex": 0,
                      "explanation": "Brief explanation of why this answer is correct."
                    }
                  ]
                }

                Rules:
                - Questions must test genuine understanding, not just recall
                - All 4 options must be plausible
                - correctIndex is the 0-based index of the correct option
                - Close all braces and brackets
                - Return ONLY the JSON object
                """.formatted(userInput.getContent());

        return new QuizJson(
                context.ai()
                        .withDefaultLlm()
                        .withPromptContributor(TUTOR)
                        .generateText(prompt)
        );
    }
}