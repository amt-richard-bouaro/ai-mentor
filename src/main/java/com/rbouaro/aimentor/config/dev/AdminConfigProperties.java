package com.rbouaro.aimentor.config.dev;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "app.admin")
public record AdminConfigProperties(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password
) {}