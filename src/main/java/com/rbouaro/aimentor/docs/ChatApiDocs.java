package com.rbouaro.aimentor.docs;

import com.rbouaro.aimentor.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Chat API",
        description = "AI Mentor conversational interface"
)
@RequestMapping("/api/v1/chat")
public interface ChatApiDocs {

    // ===================== CHAT =====================

    @Operation(
            summary = "Send a message to AI Mentor",
            description = "Processes a user message and returns an AI-generated response"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Message processed successfully",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Chat Response",
                            value = """
                                    {
                                      "response": "Spring Boot is a framework that simplifies Java application development."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Empty or invalid message",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Empty Message",
                            value = """
                                    {
                                      "error": "Message cannot be empty"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error while processing message",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Processing Error",
                            value = """
                                    {
                                      "error": "An error occurred while processing your message"
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    ResponseEntity<Map<String, String>> processMessage(
            @AuthenticationPrincipal User user,
            @RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Chat Request",
                                    value = """
                                            {
                                              "message": "Explain Spring Security in simple terms"
                                            }
                                            """
                            )
                    )
            )
            Map<String, String> request
    );

    // ===================== HEALTH =====================

    @Operation(
            summary = "Chat service health check",
            description = "Verifies that the AI Mentor chat service is running"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Health Response",
                            value = """
                                    {
                                      "status": "UP",
                                      "message": "AI Mentor is ready to help you learn!"
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/health")
    ResponseEntity<Map<String, String>> healthCheck();
}
