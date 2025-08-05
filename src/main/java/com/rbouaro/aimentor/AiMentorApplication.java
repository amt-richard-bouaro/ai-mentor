package com.rbouaro.aimentor;

import com.rbouaro.aimentor.config.properties.CorsConfigProperties;
import com.rbouaro.aimentor.config.properties.YoutubeAPIConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({YoutubeAPIConfigProperties.class, CorsConfigProperties.class})
public class AiMentorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMentorApplication.class, args);
    }

}