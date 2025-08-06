package com.rbouaro.aimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbouaro.aimentor.config.properties.YoutubeAPIConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToolService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final YoutubeAPIConfigProperties youtubeProperties;

    public List<Map<String, String>> searchYouTubeVideos(String query, int maxResults) {
        log.info("Searching YouTube for: {}", query);
        
        String url = UriComponentsBuilder
                .fromHttpUrl(youtubeProperties.baseUrl() + "/search")
                .queryParam("part", "snippet")
                .queryParam("maxResults", maxResults)
                .queryParam("q", query)
                .queryParam("type", "video")
                .queryParam("key", youtubeProperties.apiKey())
                .build()
                .toUriString();
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("items");
            
            List<Map<String, String>> results = new ArrayList<>();
            
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String videoId = item.path("id").path("videoId").asText();
                    String title = item.path("snippet").path("title").asText();
                    String description = item.path("snippet").path("description").asText();
                    String thumbnailUrl = item.path("snippet").path("thumbnails").path("medium").path("url").asText();
                    String channelTitle = item.path("snippet").path("channelTitle").asText();
                    
                    Map<String, String> video = new HashMap<>();
                    video.put("id", videoId);
                    video.put("title", title);
                    video.put("description", description);
                    video.put("thumbnailUrl", thumbnailUrl);
                    video.put("channelTitle", channelTitle);
                    video.put("url", "https://www.youtube.com/watch?v=" + videoId);
                    
                    results.add(video);
                }
            }
            
            return results;
        } catch (Exception e) {
            log.error("Error searching YouTube: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    public List<Map<String, String>> searchGitHubProjects(String query, int maxResults) {
        // This would be implemented to search GitHub for projects
        // For now, we'll return an empty list
        log.info("GitHub search not implemented yet");
        return List.of();
    }
    
    public List<Map<String, String>> searchForResources(String query, String type, int maxResults) {
        switch (type.toUpperCase()) {
            case "VIDEO":
                return searchYouTubeVideos(query, maxResults);
            case "GITHUB_PROJECT":
                return searchGitHubProjects(query, maxResults);
            default:
                log.info("Search type not supported: {}", type);
                return List.of();
        }
    }
}