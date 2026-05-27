package com.rbouaro.aimentor.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A question with the correct answer and user's selection revealed")
public record QuizResultQuestionDto(
        @Schema(description = "Question text") String question,
        @Schema(description = "Four answer options") List<String> options,
        @Schema(description = "0-based index of the correct option") int correctIndex,
        @Schema(description = "0-based index of the option the user selected") int selectedIndex,
        @Schema(description = "Whether the user's answer was correct") boolean correct,
        @Schema(description = "Explanation of the correct answer") String explanation
) {}