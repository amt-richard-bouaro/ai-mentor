package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import com.rbouaro.aimentor.exceptions.ConflictException;
import com.rbouaro.aimentor.mapper.UserMapper;
import com.rbouaro.aimentor.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @Transactional
    public AppResponse<UserProfile> registerUser(UserRegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = userMapper.fromRegister(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPermissions(Set.of(UserPermission.USER));

        userRepository.save(user);

        tokenService.injectAccessToken(user, response);

        return new AppResponse<>("User registered successfully", userMapper.toResponse(user));
    }

    public AppResponse<UserProfile> getUserProfile(User user) {
        UserProfile userProfile = userMapper.toResponse(user);
        return new AppResponse<>("User profile retrieved successfully", userProfile);
    }

    public AppResponse<List<UserGoal>> getUserGoals(User user) {

        User u = userRepository.findUserId(user.getId()).orElseThrow(()-> new IllegalArgumentException("User not found"));

        return new AppResponse<>("Your goals", u.getGoals());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}