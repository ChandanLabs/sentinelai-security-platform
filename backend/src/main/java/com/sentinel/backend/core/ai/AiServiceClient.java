package com.sentinel.backend.core.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.backend.core.event.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;
import java.util.*;

@Component
public class AiServiceClient {
    private final RestClient client;
    private final ObjectMapper mapper;

    public AiServiceClient(
            RestClient.Builder builder,
            ObjectMapper mapper,
            @Value("${ai.service.url:http://localhost:8000}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.client = builder.requestFactory(requestFactory).baseUrl(baseUrl).build();
        this.mapper = mapper;
    }

    public Prediction predict(Event event) {
        try {
            Map<String, Object> eventPayload = toPayload(event);
            JsonNode node = client.post().uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("event", eventPayload))
                    .retrieve().body(JsonNode.class);
            if (node == null) return Prediction.unavailable("AI service returned an empty response.");
            return new Prediction(
                    node.path("model_ready").asBoolean(false),
                    node.path("model_version").asText("unknown"),
                    node.path("anomaly_score").isNumber() ? node.path("anomaly_score").asDouble() : null,
                    node.path("is_anomaly").isBoolean() ? node.path("is_anomaly").asBoolean() : null,
                    node.path("confidence").isNumber() ? node.path("confidence").asDouble() : null,
                    node.path("reason").asText("No explanation returned.")
            );
        } catch (Exception ex) {
            return Prediction.unavailable("AI service unavailable; event retained and rule detection continues.");
        }
    }

    public TrainResult train(List<Event> events) {
        List<Map<String, Object>> payload = events.stream().map(this::toPayload).toList();
        JsonNode node = client.post().uri("/train")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("events", payload))
                .retrieve().body(JsonNode.class);
        if (node == null) throw new IllegalStateException("AI service returned an empty training response.");
        return new TrainResult(
                node.path("model_version").asText("unknown"),
                node.path("samples").asInt(events.size()),
                node.path("message").asText("Model trained.")
        );
    }

    private Map<String, Object> toPayload(Event event) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("event_type", event.getEventType());
        map.put("severity", event.getSeverity().name());
        map.put("timestamp", event.getEventTimestamp());
        map.put("source_ip", event.getSourceIp());
        map.put("username", event.getUsername());
        map.put("message", event.getMessage());
        map.put("metadata_json", event.getMetadataJson());
        return map;
    }

    public record Prediction(boolean modelReady, String modelVersion, Double anomalyScore,
                             Boolean anomaly, Double confidence, String reason) {
        static Prediction unavailable(String reason) {
            return new Prediction(false, "unavailable", null, null, null, reason);
        }
    }

    public record TrainResult(String modelVersion, int samples, String message) {}
}
