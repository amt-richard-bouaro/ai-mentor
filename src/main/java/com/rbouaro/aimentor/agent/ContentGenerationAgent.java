package com.rbouaro.aimentor.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;

@Agent(description = "Generate a rich Tiptap JSON document for a given learning topic or roadmap")
public class ContentGenerationAgent {

    static final RoleGoalBackstory TUTOR = new RoleGoalBackstory(
            "AI Learning Mentor",
            "Help users understand learning topics through rich, engaging content",
            "Experienced educator with deep knowledge across technology and professional skills"
    );

    public record TiptapContent(String content) {}

    @AchievesGoal(description = "A valid Tiptap JSON document has been generated")
    @Action
    TiptapContent generate(UserInput userInput, OperationContext context) {
        String prompt = """
                %s

                Format the above as a Tiptap JSON document. Use headings, paragraphs, bullet lists,
                bold/italic marks, blockquotes (for tips), and emojis where appropriate.

                Return ONLY a valid JSON object — no markdown, no extra text:
                {"type":"doc","content":[
                  {"type":"heading","attrs":{"level":2},"content":[{"type":"text","text":"Title"}]},
                  {"type":"paragraph","content":[{"type":"text","text":"..."},{"type":"text","marks":[{"type":"bold"}],"text":"bold"}]},
                  {"type":"bulletList","content":[{"type":"listItem","content":[{"type":"paragraph","content":[{"type":"text","text":"item"}]}]}]},
                  {"type":"blockquote","content":[{"type":"paragraph","content":[{"type":"text","text":"💡 tip"}]}]}
                ]}

                Supported marks: bold, italic, code. Close all braces and brackets.
                """.formatted(userInput.getContent());

        return new TiptapContent(
                context.ai()
                        .withDefaultLlm()
                        .withPromptContributor(TUTOR)
                        .generateText(prompt)
        );
    }
}