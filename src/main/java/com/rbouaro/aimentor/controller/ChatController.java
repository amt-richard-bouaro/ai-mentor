package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.documentation.ChatApi;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.exceptions.BadRequestException;
import com.rbouaro.aimentor.service.AssistantOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController implements ChatApi {

    private final AssistantOrchestrator assistantOrchestrator;

    @Override
    public AppResponse<String> processMessage(User user, Map<String, String> request) {
        String userId = user != null ? user.getId().toString() : "anonymous";
        log.info("Received message from user {}", userId);

        String userMessage = request.getOrDefault("message", "");
        if (userMessage.isBlank()) {
            throw new BadRequestException("Message cannot be empty");
        }

        String response = assistantOrchestrator.processUserInput(userId, userMessage);
        return new AppResponse<>("OK", response);
    }

    @Override
    public AppResponse<String> healthCheck() {
        return new AppResponse<>("AI Mentor is ready to help you learn!", "UP");
    }
}
