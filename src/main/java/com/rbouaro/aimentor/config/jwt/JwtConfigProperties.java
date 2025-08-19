package com.rbouaro.aimentor.config.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfigProperties(

        @NotBlank(message = "Jwt secret is required")
        @Size(
                min = 32, message = "Jwt secret must be 32 characters",
                max = 32
        )
        String jwtSecret,

        long jwtExpiration // in minute
) {
}