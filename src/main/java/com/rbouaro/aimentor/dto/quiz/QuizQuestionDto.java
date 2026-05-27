package com.rbouaro.aimentor.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A single quiz question (correct answer not included)")
public record QuizQuestionDto(
        @Schema(description = "0-based position of this question") int index,
        @Schema(description = "Question text") String question,
        @Schema(description = "Four answer options") List<String> options
) {}
