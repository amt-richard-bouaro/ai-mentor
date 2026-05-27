package com.rbouaro.aimentor.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "User's answers to a quiz")
public record QuizSubmitRequest(
        @NotEmpty
        @Schema(description = "Selected option index (0-based) for each question, in order") List<Integer> answers
) {}