package com.sentinel.backend.core.ai;

import com.sentinel.backend.core.ai.dto.*;
import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AnomalyDetectionService {
    private final AiServiceClient aiClient;
    private final AnomalyDetectionRepository detections;
    private final EventRepository events;

    public AnomalyDetectionService(AiServiceClient aiClient, AnomalyDetectionRepository detections, EventRepository events) {
        this.aiClient = aiClient;
        this.detections = detections;
        this.events = events;
    }

    @Transactional
    public AnomalyDetectionResponse analyze(Event event) {
        AnomalyDetection existing = detections.findByEventId(event.getId()).orElse(null);
        if (existing != null) return AnomalyDetectionResponse.from(existing);

        AiServiceClient.Prediction prediction = aiClient.predict(event);
        AnomalyDetection detection = new AnomalyDetection();
        detection.setEvent(event);
        detection.setModelVersion(prediction.modelVersion());
        detection.setAnomalyScore(prediction.anomalyScore());
        detection.setAnomaly(prediction.anomaly());
        detection.setConfidence(prediction.confidence());
        detection.setReason(prediction.reason());
        detection.setStatus(prediction.modelReady() ? "SCORED" : "UNAVAILABLE");
        return AnomalyDetectionResponse.from(detections.save(detection));
    }

    @Transactional(readOnly = true)
    public Page<AnomalyDetectionResponse> all(int page, int size) {
        return detections.findAllByOrderByDetectedAtDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(AnomalyDetectionResponse::from);
    }

    @Transactional(readOnly = true)
    public AnomalyDetectionResponse byEvent(UUID eventId) {
        if (!events.existsById(eventId)) throw new NoSuchElementException("Event not found");
        return detections.findByEventId(eventId)
                .map(AnomalyDetectionResponse::from)
                .orElseThrow(() -> new NoSuchElementException("AI detection not available for event"));
    }

    @Transactional
    public TrainModelResponse train(Integer sampleSize) {
        int size = sampleSize == null ? 500 : Math.min(sampleSize, 10000);
        List<Event> trainingEvents = events.findAllByOrderByEventTimestampDesc(PageRequest.of(0, size)).getContent();
        if (trainingEvents.size() < 20) {
            throw new IllegalStateException("At least 20 events are required to train the AI baseline.");
        }
        AiServiceClient.TrainResult result = aiClient.train(trainingEvents);
        return new TrainModelResponse(result.modelVersion(), result.samples(), result.message());
    }
}
