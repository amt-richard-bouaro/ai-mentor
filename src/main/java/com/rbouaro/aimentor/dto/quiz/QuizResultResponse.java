package com.rbouaro.aimentor.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of a quiz submission")
public record QuizResultResponse(
        @Schema(description = "Number of correct answers") int score,
        @Schema(description = "Total number of questions") int total,
        @Schema(description = "Score as a percentage") double percentage,
        @Schema(description = "Whether the user passed (>= 60%)") boolean passed,
        @Schema(description = "Each question with the correct answer and user's selection revealed") List<QuizResultQuestionDto> questions
) {}