package com.rbouaro.aimentor;

import com.rbouaro.aimentor.config.cors.CorsConfigProperties;
import com.rbouaro.aimentor.config.jwt.JwtConfigProperties;
import com.rbouaro.aimentor.config.jwt.RSAConfigProperties;
import com.rbouaro.aimentor.config.youtube.YoutubeAPIConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({YoutubeAPIConfigProperties.class, CorsConfigProperties.class, JwtConfigProperties.class, RSAConfigProperties.class})
public class AiMentorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMentorApplication.class, args);
    }

}