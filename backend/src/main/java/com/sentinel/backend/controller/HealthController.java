package com.sentinel.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public HealthController(
            RestTemplate restTemplate,
            @Value("${AI_SERVICE_URL:http://ai-service:8000}") String aiServiceUrl) {
        this.restTemplate = restTemplate;
        this.aiServiceUrl = aiServiceUrl;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        String aiServiceStatus = "UNAVAILABLE";
        try {
            restTemplate.getForEntity(aiServiceUrl + "/health", String.class);
            aiServiceStatus = "AVAILABLE";
        } catch (RestClientException ignored) {
            // The response reports service availability explicitly; inference remains unconfigured.
        }
        return Map.of(
                "status", "UP",
                "aiService", aiServiceStatus,
                "aiInference", "NOT_CONFIGURED");
    }
}
