package com.rbouaro.aimentor.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Quiz with questions — correct answers are not included until submission")
public record QuizResponse(
        @Schema(description = "Quiz UUID — required when submitting answers") UUID id,
        @Schema(description = "Quiz questions") List<QuizQuestionDto> questions
) {}
