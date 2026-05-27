package com.rbouaro.aimentor.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpClientService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public JsonNode getJson(String url) {
        log.debug("[HTTP] GET {}", url);
        String body = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            log.error("[HTTP] Failed to parse JSON response from {}", url, e);
            throw new RuntimeException("Failed to parse HTTP response as JSON", e);
        }
    }
}