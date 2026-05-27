package com.rbouaro.aimentor.service;

import com.embabel.agent.api.annotation.LlmTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.rbouaro.aimentor.config.youtube.YoutubeAPIConfigProperties;
import com.rbouaro.aimentor.util.HttpClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToolService {

    private final HttpClientService httpClientService;
    private final YoutubeAPIConfigProperties youtubeProperties;

    @LlmTool(description = "Search YouTube for video tutorials on a given learning topic. Returns real videos with titles, descriptions, and watch URLs.")
    public List<Map<String, String>> searchYouTubeVideos(
            @LlmTool.Param(description = "The topic or skill to search for") String query,
            @LlmTool.Param(description = "Maximum number of results to return (1-5)") int maxResults) {
        log.info("[TOOL] Searching YouTube for: {}", query);

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
            JsonNode root = httpClientService.getJson(url);
            JsonNode items = root.path("items");

            List<Map<String, String>> results = new ArrayList<>();

            if (items.isArray()) {
                for (JsonNode item : items) {
                    String videoId = item.path("id").path("videoId").asText();
                    String title = item.path("snippet").path("title").asText();
                    String description = item.path("snippet").path("description").asText();
                    String channelTitle = item.path("snippet").path("channelTitle").asText();
                    String thumbnail = item.path("snippet").path("thumbnails").path("high").path("url").asText("");

                    Map<String, String> video = new HashMap<>();
                    video.put("title", title);
                    video.put("description", description);
                    video.put("channelTitle", channelTitle);
                    video.put("url", "https://www.youtube.com/watch?v=" + videoId);
                    video.put("thumbnail", thumbnail);
                    results.add(video);
                }
            }

            log.info("[TOOL] YouTube search returned {} results for '{}'", results.size(), query);
            return results;
        } catch (Exception e) {
            log.error("[TOOL] YouTube search failed for '{}': {}", query, e.getMessage(), e);
            return List.of();
        }
    }

    public List<Map<String, String>> searchGitHubProjects(String query, int maxResults) {
        log.info("GitHub search not implemented yet");
        return List.of();
    }

    public List<Map<String, String>> searchForResources(String query, String type, int maxResults) {
        return switch (type.toUpperCase()) {
            case "VIDEO" -> searchYouTubeVideos(query, maxResults);
            case "GITHUB_PROJECT" -> searchGitHubProjects(query, maxResults);
            default -> {
                log.info("Search type not supported: {}", type);
                yield List.of();
            }
        };
    }
}