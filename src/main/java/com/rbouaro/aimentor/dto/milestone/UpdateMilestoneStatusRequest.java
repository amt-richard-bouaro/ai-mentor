package com.rbouaro.aimentor.dto.milestone;

import com.rbouaro.aimentor.entity.Milestone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body to update a milestone's status")
public record UpdateMilestoneStatusRequest(

        @NotNull(message = "Status is required")
        @Schema(description = "New status", allowableValues = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED"})
        Milestone.MilestoneStatus status

) {}
