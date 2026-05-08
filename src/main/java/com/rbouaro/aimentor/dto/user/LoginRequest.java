package com.rbouaro.aimentor.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login credentials")
public record LoginRequest(
        @Schema(description = "Account username", example = "johndoe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Account password", example = "secret123")
        @NotBlank(message = "Password is required")
        String password
) {
}