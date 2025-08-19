package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.config.jwt.JwtConfigProperties;
import com.rbouaro.aimentor.config.jwt.JwtTokenGenerator;
import com.rbouaro.aimentor.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public final class TokenService {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtConfigProperties jwtConfigProperties;

    public void injectAccessToken(User user, HttpServletResponse response) {
        String token = jwtTokenGenerator.generateToken(user);

        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        int maxAgeSeconds = (int) Duration.ofMinutes(jwtConfigProperties.jwtExpiration()).getSeconds();
        cookie.setMaxAge(maxAgeSeconds);

        response.addCookie(cookie);
    }
    
    public void removeAccessToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    
    
}