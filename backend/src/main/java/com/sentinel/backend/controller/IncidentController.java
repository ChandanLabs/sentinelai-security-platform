package com.sentinel.backend.controller;

import com.sentinel.backend.dto.IncidentResponse;
import com.sentinel.backend.dto.IncidentStatusRequest;
import com.sentinel.backend.dto.PageResponse;
import com.sentinel.backend.service.SecurityEventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final SecurityEventService service;
    private final DemoWriteKey writeKey;

    public IncidentController(SecurityEventService service, DemoWriteKey writeKey) {
        this.service = service;
        this.writeKey = writeKey;
    }

    @GetMapping
    public PageResponse<IncidentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "page must be non-negative and size must be 1..100");
        }
        return PageResponse.from(service.listIncidents(PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Demo-Ingestion-Key", required = false) String suppliedKey,
            @Valid @RequestBody IncidentStatusRequest request) {
        writeKey.requireValid(suppliedKey);
        try {
            return service.updateIncidentStatus(id, request.status());
        } catch (java.util.NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
