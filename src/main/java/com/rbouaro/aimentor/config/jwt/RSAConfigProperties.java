package com.rbouaro.aimentor.config.jwt;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.rsa")
public record RSAConfigProperties(

        @NotBlank(message = "Public key is required")
        String publicKey,

        @NotBlank(message = "Private key is required")
        String privateKey

) {
}