package com.rbouaro.aimentor.dto.user;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "User profile information")
public record UserProfile(
        @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique username", example = "johndoe")
        String username,

        @Schema(description = "User email address", example = "john@example.com")
        String email,

        @Schema(description = "Set of granted permissions")
        Set<UserPermission> permissions,

        @Schema(description = "Account creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp")
        LocalDateTime lastModifiedAt
) {
}