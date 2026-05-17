package com.rbouaro.aimentor.documentation;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.milestone.MilestoneResponse;
import com.rbouaro.aimentor.dto.roadmap.RoadmapResponse;
import com.rbouaro.aimentor.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Roadmaps", description = "View roadmaps and their milestones")
public interface RoadmapApi {

    @Operation(summary = "List roadmaps", description = "Returns all roadmaps belonging to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roadmaps retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<List<RoadmapResponse>> listRoadmaps(@AuthenticationPrincipal User user);

    @Operation(summary = "Get roadmap", description = "Returns a single roadmap by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roadmap retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Roadmap not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<RoadmapResponse> getRoadmap(@AuthenticationPrincipal User user, @PathVariable UUID id);

    @Operation(summary = "List milestones", description = "Returns all milestones for the given roadmap, ordered by index.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Milestones retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Roadmap not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/milestones", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<List<MilestoneResponse>> listMilestones(@AuthenticationPrincipal User user, @PathVariable UUID id);

    @Operation(summary = "Get progress", description = "Returns the completion percentage (0–100) for the given roadmap.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Roadmap not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/progress", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<Double> getProgress(@AuthenticationPrincipal User user, @PathVariable UUID id);
}
