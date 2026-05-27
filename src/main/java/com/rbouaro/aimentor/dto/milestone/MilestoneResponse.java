package com.rbouaro.aimentor.dto.milestone;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.rbouaro.aimentor.dto.Views;
import com.rbouaro.aimentor.dto.resource.ResourceResponse;
import com.rbouaro.aimentor.entity.Milestone;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Milestone details")
public record MilestoneResponse(

        @Schema(description = "Milestone UUID") UUID id,
        @Schema(description = "Parent roadmap UUID") UUID roadmapId,
        @Schema(description = "Milestone title") String title,
        @Schema(description = "Milestone description") String description,
        @JsonView(Views.Detail.class) @JsonRawValue @Schema(description = "Milestone content in Tiptap JSON format") String content,
        @Schema(description = "Current status") Milestone.MilestoneStatus status,
        @Schema(description = "Order within the roadmap") Integer orderIndex,
        @Schema(description = "Learning resources for this milestone") List<ResourceResponse> resources,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp") LocalDateTime updatedAt,
        @Schema(description = "Completion timestamp, null if not completed") LocalDateTime completedAt

) {}