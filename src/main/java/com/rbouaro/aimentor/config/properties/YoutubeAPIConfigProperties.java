package com.rbouaro.aimentor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "extension.youtube")
public record YoutubeAPIConfigProperties(
        String apiKey,
        String baseUrl) {
}