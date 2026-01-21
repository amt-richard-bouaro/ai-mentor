package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.global.PaginatedResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public AppResponse<UserProfile> registerUser(UserRegisterRequest request, HttpServletResponse response);

    public AppResponse<UserProfile> getUserProfile(User user);

    public AppResponse<PaginatedResponse<UserProfile>> getAllUsers(String searchTerm, int page, int size);

    public AppResponse<List<UserGoal>> getUserGoals(User user);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

}