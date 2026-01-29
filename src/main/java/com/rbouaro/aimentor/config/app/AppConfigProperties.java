package com.rbouaro.aimentor.config.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email")
public record AppConfigProperties(

        RetryConfigProperties retry,
        String from
) {
    public record RetryConfigProperties(
            int maxAttempts,
            long initialDelay
    ) {
    }
}
