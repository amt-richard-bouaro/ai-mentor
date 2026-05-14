package com.rbouaro.aimentor.documentation;

import com.rbouaro.aimentor.dto.chat.ChatMessageRequest;
import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "AI mentor chat and health check")
public interface ChatApi {

    @Operation(summary = "Send message", description = "Send a message to the AI mentor and receive a response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response from the AI mentor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Message is empty",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "AI processing error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<String> processMessage(@AuthenticationPrincipal User user, @Valid @RequestBody ChatMessageRequest request);

    @Operation(summary = "Health check", description = "Check if the AI mentor service is up and ready.")
    @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppResponse.class)))
    @SecurityRequirements
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<String> healthCheck();
}