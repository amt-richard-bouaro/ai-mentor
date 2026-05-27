package com.rbouaro.aimentor.dto.roadmap;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.rbouaro.aimentor.dto.Views;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Roadmap details")
public record RoadmapResponse(

        @Schema(description = "Roadmap UUID") UUID id,
        @Schema(description = "Roadmap title") String title,
        @Schema(description = "Roadmap description") String description,
        @JsonView(Views.Detail.class) @JsonRawValue @Schema(description = "Roadmap overview in Tiptap JSON format") String content,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp") LocalDateTime updatedAt

) {}