package com.rbouaro.aimentor.dto.user;

import com.rbouaro.aimentor.constants.enums.UserPermission;

import java.time.LocalDateTime;
import java.util.Set;

public record UserProfile(
        Long id,
        String username,
        String email,
        Set<UserPermission> permissions,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {

}