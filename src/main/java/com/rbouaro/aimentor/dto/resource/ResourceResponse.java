package com.rbouaro.aimentor.dto.resource;

import com.rbouaro.aimentor.entity.Resource;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Learning resource details")
public record ResourceResponse(

        @Schema(description = "Resource UUID") UUID id,
        @Schema(description = "Parent milestone UUID") UUID milestoneId,
        @Schema(description = "Resource title") String title,
        @Schema(description = "Why this resource is valuable") String description,
        @Schema(description = "URL to the resource") String url,
        @Schema(description = "Thumbnail image URL, available for VIDEO resources") String thumbnail,
        @Schema(description = "Resource type") Resource.ResourceType type,
        @Schema(description = "Current status") Resource.ResourceStatus status,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp") LocalDateTime updatedAt,
        @Schema(description = "Completion timestamp, null if not completed") LocalDateTime completedAt

) {}