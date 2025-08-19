package com.rbouaro.aimentor.dto.auth;

import com.rbouaro.aimentor.dto.user.UserProfile;

public record AuthResponse(
        String token,
        UserProfile user
) { }