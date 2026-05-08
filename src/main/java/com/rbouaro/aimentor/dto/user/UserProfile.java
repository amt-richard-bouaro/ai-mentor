package com.rbouaro.aimentor.dto.user;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "User profile information")
public record UserProfile(
        @Schema(description = "Internal user ID", example = "1")
        Long id,

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