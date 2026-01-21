package com.rbouaro.aimentor.config.dev;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.dev")
public record DevConfigProperties(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password
) {
}
