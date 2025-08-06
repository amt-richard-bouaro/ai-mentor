package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.service.AssistantOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = ChatControllerTest.TestConfig.class)
public class ChatControllerTest {

    @Configuration
    @Import(ChatController.class)
    static class TestConfig {
        @Bean
        public AssistantOrchestrator assistantOrchestrator() {
            return mock(AssistantOrchestrator.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssistantOrchestrator assistantOrchestrator;

    @Test
    public void healthCheckShouldReturnOk() throws Exception {
        mockMvc.perform(get("/chat/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("AI Mentor is ready to help you learn!"));
    }

    @Test
    public void processMessageShouldReturnResponse() throws Exception {
        // Given
        String testResponse = "This is a test response from the assistant";
        when(assistantOrchestrator.processUserInput(anyString(), anyString())).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/chat/message")
                .header("X-User-Id", "testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"I want to learn Java\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(testResponse));
    }

    @Test
    public void emptyMessageShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/chat/message")
                .header("X-User-Id", "testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Message cannot be empty"));
    }

    @Test
    public void exceptionShouldReturnInternalServerError() throws Exception {
        // Given
        when(assistantOrchestrator.processUserInput(anyString(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

        // When & Then
        mockMvc.perform(post("/chat/message")
                .header("X-User-Id", "testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"I want to learn Java\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An error occurred while processing your message: Test exception"));
    }
}