package com.rbouaro.aimentor.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank(message = "Message cannot be empty")
        @Schema(description = "The message to send to the AI mentor", example = "I want to learn Kubernetes from scratch")
        String message
) {}