package com.rbouaro.aimentor.dto.user;

import com.rbouaro.aimentor.constants.enums.UserPermission;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserProfile(
        UUID id,
        String username,
        String email,
        Set<UserPermission> permissions,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {

}