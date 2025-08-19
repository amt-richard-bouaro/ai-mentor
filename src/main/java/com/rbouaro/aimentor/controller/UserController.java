package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import com.rbouaro.aimentor.service.MemoryService;
import com.rbouaro.aimentor.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final MemoryService memoryService;

    public UserController(UserService userService, MemoryService memoryService) {
        this.userService = userService;
        this.memoryService = memoryService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<UserProfile> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest,
                                             HttpServletResponse response
    ) {
        log.info("Request received to create user with username {}", userRegisterRequest.username());
        return userService.registerUser(userRegisterRequest, response
        );
    }

    @GetMapping("/me")
    public AppResponse<UserProfile> getCurrentUser(@AuthenticationPrincipal User user) {
        log.info("Request received to get user profile for user {}", user.getUsername());
        return userService.getUserProfile(user);
    }

    @GetMapping("/me/goals")
    public ResponseEntity<List<UserGoal>> getCurrentUserGoals(@AuthenticationPrincipal User user) {
        log.info("Request received to get user goals for user {}", user.getUsername());
        return ResponseEntity.ok(user.getGoals());
    }

    @PostMapping("/goals")
    public ResponseEntity<UserGoal> createGoal(@AuthenticationPrincipal User user, @RequestBody Map<String, String> request) {
        String title = request.getOrDefault("title", "");
        String description = request.getOrDefault("description", "");

        if (title.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        UserGoal goal = memoryService.createGoal(user, title, description);
        return ResponseEntity.ok(goal);
    }
}