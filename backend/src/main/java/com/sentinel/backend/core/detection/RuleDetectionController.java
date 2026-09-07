package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.detection.dto.RuleDetectionResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detections/rules")
public class RuleDetectionController {
    private final RuleDetectionService service;

    public RuleDetectionController(RuleDetectionService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<RuleDetectionResponse> all(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
        return service.all(page, size);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<RuleDetectionResponse> byEvent(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
        return service.byEvent(eventId, page, size);
    }
}
