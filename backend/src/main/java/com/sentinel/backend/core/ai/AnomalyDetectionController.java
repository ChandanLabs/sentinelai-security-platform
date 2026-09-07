package com.sentinel.backend.core.ai;

import com.sentinel.backend.core.ai.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detections/ai")
public class AnomalyDetectionController {
    private final AnomalyDetectionService service;

    public AnomalyDetectionController(AnomalyDetectionService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<AnomalyDetectionResponse> all(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
        return service.all(page, size);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public AnomalyDetectionResponse byEvent(@PathVariable UUID eventId) {
        return service.byEvent(eventId);
    }

    @PostMapping("/train")
    @PreAuthorize("hasRole('ADMIN')")
    public TrainModelResponse train(@Valid @RequestBody(required = false) TrainModelRequest request) {
        return service.train(request == null ? 500 : request.sampleSize());
    }
}
