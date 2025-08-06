package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.model.User;
import com.rbouaro.aimentor.model.UserGoal;
import com.rbouaro.aimentor.service.MemoryService;
import com.rbouaro.aimentor.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MemoryService memoryService;

    public UserController(UserService userService, MemoryService memoryService) {
        this.userService = userService;
        this.memoryService = memoryService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/goals")
    public ResponseEntity<List<UserGoal>> getCurrentUserGoals(@AuthenticationPrincipal User user) {
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