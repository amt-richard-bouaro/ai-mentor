package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.global.PaginatedResponse;
import com.rbouaro.aimentor.dto.goal.UserGoalResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    AppResponse<UserProfile> registerUser(UserRegisterRequest request, HttpServletResponse response);

    AppResponse<UserProfile> getUserProfile(User user);

    AppResponse<PaginatedResponse<UserProfile>> getAllUsers(String searchTerm, int page, int size);

    AppResponse<UserProfile> getUserById(UUID id);

    AppResponse<Void> deleteUser(User user, HttpServletResponse response);

    AppResponse<List<UserGoalResponse>> getUserGoals(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}