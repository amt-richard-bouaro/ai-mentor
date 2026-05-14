package com.rbouaro.aimentor.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateGoalRequest(
        @NotBlank(message = "Title is required")
        @Schema(description = "The title of the learning goal", example = "Learn Spring Boot")
        String title,

        @Schema(description = "Additional context or description for the learning goal", example = "I want to build REST APIs with Spring Boot and deploy them to the cloud")
        String description
) {}