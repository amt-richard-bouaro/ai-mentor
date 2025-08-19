package com.rbouaro.aimentor.config.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsConfigProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods
        , List<String> allowedHeaders

) {
}