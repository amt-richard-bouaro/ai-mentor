package com.rbouaro.aimentor.dto.goal;

import com.rbouaro.aimentor.constants.enums.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User learning goal")
public record UserGoalResponse(

        @Schema(description = "Goal UUID") UUID id,
        @Schema(description = "Goal title") String title,
        @Schema(description = "Goal description") String description,
        @Schema(description = "Current status") GoalStatus status,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp") LocalDateTime updatedAt

) {}