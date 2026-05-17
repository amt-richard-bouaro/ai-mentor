package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.documentation.UserApi;
import com.rbouaro.aimentor.dto.chat.ChatMessageRequest;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.goal.CreateGoalRequest;
import com.rbouaro.aimentor.dto.goal.UserGoalResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import com.rbouaro.aimentor.mapper.UserGoalMapper;
import com.rbouaro.aimentor.service.AssistantOrchestrator;
import com.rbouaro.aimentor.service.MemoryService;
import com.rbouaro.aimentor.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final MemoryService memoryService;
    private final UserGoalMapper userGoalMapper;
    private final AssistantOrchestrator assistantOrchestrator;

    public UserController(UserService userService, MemoryService memoryService,
                          UserGoalMapper userGoalMapper, AssistantOrchestrator assistantOrchestrator) {
        this.userService = userService;
        this.memoryService = memoryService;
        this.userGoalMapper = userGoalMapper;
        this.assistantOrchestrator = assistantOrchestrator;
    }

    @Override
    public AppResponse<UserProfile> register(UserRegisterRequest userRegisterRequest, HttpServletResponse response) {
        log.info("Request received to create user with username {}", userRegisterRequest.username());
        return userService.registerUser(userRegisterRequest, response);
    }

    @Override
    public AppResponse<UserProfile> getCurrentUser(User user) {
        log.info("Request received to get user profile for user {}", user.getUsername());
        return userService.getUserProfile(user);
    }

    @Override
    public AppResponse<List<UserGoalResponse>> getCurrentUserGoals(User user) {
        log.info("Request received to get user goals for user {}", user.getUsername());
        return userService.getUserGoals(user);
    }

    @Override
    public AppResponse<UserGoalResponse> createGoal(User user, CreateGoalRequest request) {
        UserGoal goal = memoryService.createGoal(user, request.title(), request.description());
        return new AppResponse<>("Goal created", userGoalMapper.toResponse(goal));
    }

    @Override
    public AppResponse<String> chat(User user, ChatMessageRequest request) {
        log.info("[CHAT] Received message from userId={}", user.getId());
        String response = assistantOrchestrator.processUserInput(user.getId().toString(), request.message());
        log.info("[CHAT] Responding to userId={} responseLength={}", user.getId(), response.length());
        return new AppResponse<>("OK", response);
    }

    @Override
    public AppResponse<String> chatHealth() {
        return new AppResponse<>("AI Mentor is ready to help you learn!", "UP");
    }
}