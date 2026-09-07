package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.detection.dto.RuleDetectionResponse;
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
public class RuleDetectionService {
    private final RuleEngine ruleEngine;
    private final RuleDetectionRepository detections;
    private final EventRepository events;

    public RuleDetectionService(RuleEngine ruleEngine, RuleDetectionRepository detections, EventRepository events) {
        this.ruleEngine = ruleEngine;
        this.detections = detections;
        this.events = events;
    }

    @Transactional
    public List<RuleDetectionResponse> evaluate(Event event) {
        return ruleEngine.evaluate(event).stream()
                .filter(match -> !detections.existsByEventIdAndRuleCode(event.getId(), match.code()))
                .map(match -> save(event, match))
                .map(RuleDetectionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RuleDetectionResponse> all(int page, int size) {
        return detections.findAllByOrderByDetectedAtDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(RuleDetectionResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<RuleDetectionResponse> byEvent(UUID eventId, int page, int size) {
        if (!events.existsById(eventId)) throw new NoSuchElementException("Event not found");
        return detections.findByEventIdOrderByDetectedAtDesc(eventId, PageRequest.of(page, Math.min(size, 100)))
                .map(RuleDetectionResponse::from);
    }

    private RuleDetection save(Event event, RuleMatch match) {
        RuleDetection detection = new RuleDetection();
        detection.setEvent(event);
        detection.setRuleCode(match.code());
        detection.setRuleName(match.name());
        detection.setSeverity(match.severity());
        detection.setReason(match.reason());
        return detections.save(detection);
    }
}
