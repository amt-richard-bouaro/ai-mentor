package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.docs.AuthApiDocs;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.LoginRequest;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {


    private final AuthService authService;

    @PostMapping("/login")
    public AppResponse<UserProfile> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        log.info("Received login request for user {}", loginRequest.username());
       return authService.login(loginRequest, response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        log.info("Received logout request");
        authService.logout(response);
    }
}