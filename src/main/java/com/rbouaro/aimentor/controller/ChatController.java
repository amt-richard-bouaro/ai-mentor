package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.service.AssistantOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final AssistantOrchestrator assistantOrchestrator;

    @PostMapping
    public ResponseEntity<Map<String, String>> processMessage(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {

        String userId = user != null ? user.getId().toString() : "anonymous";
        log.info("Received message from user {}", userId);

        String userMessage = request.getOrDefault("message", "");
        if (userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Message cannot be empty"
            ));
        }

        try {
            String response = assistantOrchestrator.processUserInput(userId, userMessage);

            return ResponseEntity.ok(Map.of(
                    "response", response
            ));
        } catch (Exception e) {
            log.error("Error processing message", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "An error occurred while processing your message: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "AI Mentor is ready to help you learn!"
        ));
    }
}