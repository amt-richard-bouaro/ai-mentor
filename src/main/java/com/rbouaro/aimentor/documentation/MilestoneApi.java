package com.rbouaro.aimentor.documentation;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.milestone.MilestoneResponse;
import com.rbouaro.aimentor.dto.milestone.UpdateMilestoneStatusRequest;
import com.rbouaro.aimentor.dto.resource.ResourceResponse;
import com.rbouaro.aimentor.dto.resource.UpdateResourceStatusRequest;
import com.rbouaro.aimentor.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Milestones", description = "Manage milestones and their learning resources")
public interface MilestoneApi {

    @Operation(summary = "Get milestone", description = "Returns a single milestone by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Milestone retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Milestone not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<MilestoneResponse> getMilestone(@AuthenticationPrincipal User user, @PathVariable UUID id);

    @Operation(summary = "Update milestone status", description = "Updates the status of a milestone (NOT_STARTED, IN_PROGRESS, COMPLETED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid status value",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Milestone not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<MilestoneResponse> updateMilestoneStatus(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMilestoneStatusRequest request);

    @Operation(summary = "List resources", description = "Returns all learning resources for a milestone. Triggers on-demand generation if none exist yet.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Milestone not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<List<ResourceResponse>> listResources(@AuthenticationPrincipal User user, @PathVariable UUID id);

    @Operation(summary = "Update resource status", description = "Updates the status of a specific learning resource.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid status value",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource or milestone not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @PatchMapping(value = "/{id}/resources/{resourceId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<ResourceResponse> updateResourceStatus(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateResourceStatusRequest request);
}
