package com.rbouaro.aimentor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    private static final String MODEL_NAME = "llama3.2";

    private static final String SYSTEM_PROMPT = """
            You are an AI learning mentor that helps users create personalized learning roadmaps.
            Your goal is to understand the user's learning objectives, ask clarifying questions,
            and generate a structured learning plan with milestones and resources.
            Be helpful, encouraging, and provide specific, actionable advice.
            """;

    public String generateClarifyingQuestions(String userGoal) {
        log.info("Generating clarifying questions for goal: {}", userGoal);

        String prompt = """
                The user has expressed interest in learning: "%s".

                Generate 3-5 clarifying questions to better understand their:
                1. Current skill level and background
                2. Specific areas of interest within the broader topic
                3. Time commitment and learning pace
                4. Preferred learning style (hands-on projects, theory, etc.)
                5. End goal or what they want to achieve

                Format your response as a numbered list of questions only.
                """.formatted(userGoal);

        return generateCompletion(prompt);
    }

    public String generateRoadmap(String userGoal, String userContext) {
        log.info("Generating roadmap for goal: {} with context: {}", userGoal, userContext);

        String prompt = """
                Create a detailed learning roadmap for a user with the following goal:
                "%s"

                Additional context from the user:
                %s

                The roadmap should include:
                1. A title for the overall learning journey
                2. A brief description of the learning path
                3. sequential milestones (must cover all areas of the topic), each with:
                   - A clear, specific title
                   - A description of what to learn
                   - Why this milestone is important
                   - Estimated time to complete

                Format your response in JSON with the following structure:
                {
                  "title": "Roadmap title",
                  "description": "Overall description of the learning journey",
                  "milestones": [
                    {
                      "title": "Milestone 1 title",
                      "description": "Detailed description of what to learn",
                      "importance": "Why this milestone matters",
                      "estimatedTimeInHours": 10
                    },
                    // more milestones...
                  ]
                }

                IMPORTANT: Return ONLY the raw JSON without any additional text, comments, or explanations.
                Do not wrap the JSON in markdown code blocks or add any prefixes/suffixes.
                """.formatted(userGoal, userContext);

        return generateCompletion(prompt);
    }

    public String generateResourceRecommendations(String milestoneTitle, String milestoneDescription) {
        log.info("Generating resource recommendations for milestone: {}", milestoneTitle);

        String prompt = """
                For the following milestone in a learning roadmap:

                Title: %s
                Description: %s

                Recommend 3-5 specific learning resources that would help the user complete this milestone.
                For each resource, provide:
                1. A descriptive title
                2. A brief explanation of why it's valuable
                3. What type of resource it is (video, article, course, project, book, etc.)
                4. Keywords that could be used to search for this resource online

                Format your response in JSON with the following structure:
                {
                  "resources": [
                    {
                      "title": "Resource title",
                      "description": "Why this resource is valuable",
                      "type": "VIDEO|ARTICLE|COURSE|GITHUB_PROJECT|BOOK|OTHER",
                      "searchKeywords": ["keyword1", "keyword2", "keyword3"]
                    },
                    // more resources...
                  ]
                }
                IMPORTANT:
                  1. Return ONLY raw valid JSON - no additional text, comments, or explanations
                  2. Ensure all brackets and braces are properly closed
                  3. Do not use markdown code blocks
                  4. Make sure the JSON is complete and well-formed
                """.formatted(milestoneTitle, milestoneDescription);

        return generateCompletion(prompt);
    }

    private String generateCompletion(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("prompt", prompt);
            requestBody.put("system", SYSTEM_PROMPT);
            requestBody.put("stream", false);

            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);
            options.put("top_p", 0.9);
            requestBody.put("options", options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            String url = ollamaBaseUrl + "/api/generate";
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
            return (String) responseMap.get("response");
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for LLM request/response", e);
            throw new RuntimeException("Failed to process LLM request or response", e);
        } catch (Exception e) {
            log.error("Error generating completion from Ollama", e);
            throw new RuntimeException("Failed to generate completion from Ollama", e);
        }
    }
}