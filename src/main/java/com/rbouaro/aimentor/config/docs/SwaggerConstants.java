package com.rbouaro.aimentor.config.docs;

public final class SwaggerConstants {

    private SwaggerConstants() {}

    public static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };
}
