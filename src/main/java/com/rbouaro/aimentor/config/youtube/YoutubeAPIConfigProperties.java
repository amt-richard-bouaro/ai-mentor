package com.rbouaro.aimentor.config.youtube;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "extension.youtube")
public record YoutubeAPIConfigProperties(
        String apiKey,
        String baseUrl) {
}