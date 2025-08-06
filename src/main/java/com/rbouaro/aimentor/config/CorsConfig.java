package com.rbouaro.aimentor.config;

import com.rbouaro.aimentor.config.properties.CorsConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsConfigProperties corsProperties;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specified origins
        corsProperties.allowedOrigins().forEach(config::addAllowedOrigin);
        
        // Allow specified methods
        corsProperties.allowedMethods().forEach(config::addAllowedMethod);
        
        // Allow specified headers
        corsProperties.allowedHeaders().forEach(config::addAllowedHeader);
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}