package com.rbouaro.aimentor;

import com.rbouaro.aimentor.config.app.AppConfigProperties;
import com.rbouaro.aimentor.config.dev.AdminConfigProperties;
import com.rbouaro.aimentor.config.cors.CorsConfigProperties;
import com.rbouaro.aimentor.config.dev.DevConfigProperties;
import com.rbouaro.aimentor.config.jwt.JwtConfigProperties;
import com.rbouaro.aimentor.config.jwt.RSAConfigProperties;
import com.rbouaro.aimentor.config.youtube.YoutubeAPIConfigProperties;
import com.embabel.agent.config.annotation.EnableAgents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAgents
@EnableAsync
@EnableRetry
@EnableConfigurationProperties({
        YoutubeAPIConfigProperties.class,
        CorsConfigProperties.class,
        JwtConfigProperties.class,
        RSAConfigProperties.class,
        AdminConfigProperties.class,
        DevConfigProperties.class,
        AppConfigProperties.class
})
public class AiMentorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMentorApplication.class, args);
    }

}