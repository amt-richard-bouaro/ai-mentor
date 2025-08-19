package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.config.security.AuthUserDetails;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.LoginRequest;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.exceptions.UnauthorizedException;
import com.rbouaro.aimentor.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    private final TokenService tokenService;

    public AppResponse<UserProfile> login(LoginRequest request, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            AuthUserDetails authUserDetails = (AuthUserDetails) authentication.getPrincipal();

            User user = authUserDetails.user();

            tokenService.injectAccessToken(user, response);

            return new AppResponse<>("Login successful", userMapper.toResponse(user));

        } catch (RuntimeException e) {
            log.info("Login failed for user {}: {}", request.username(), e.getMessage());
            throw new UnauthorizedException("Invalid username or password");
        }

    }


    public void logout(HttpServletResponse response) {
        log.info("Logging out user");
        tokenService.removeAccessToken(response);
        log.info("User logged out");
    }

}