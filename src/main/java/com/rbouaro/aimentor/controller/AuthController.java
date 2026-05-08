package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.documentation.AuthApi;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.LoginRequest;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public AppResponse<UserProfile> login(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("Received login request for user {}", loginRequest.username());
        return authService.login(loginRequest, response);
    }

    @Override
    public void logout(HttpServletResponse response) {
        log.info("Received logout request");
        authService.logout(response);
    }
}
