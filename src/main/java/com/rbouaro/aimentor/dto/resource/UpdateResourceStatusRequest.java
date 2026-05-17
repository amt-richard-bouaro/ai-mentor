package com.rbouaro.aimentor.dto.resource;

import com.rbouaro.aimentor.entity.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body to update a resource's status")
public record UpdateResourceStatusRequest(

        @NotNull(message = "Status is required")
        @Schema(description = "New status", allowableValues = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED"})
        Resource.ResourceStatus status

) {}
