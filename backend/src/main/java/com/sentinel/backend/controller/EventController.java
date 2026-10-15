package com.sentinel.backend.controller;

import com.sentinel.backend.dto.EventIngestRequest;
import com.sentinel.backend.dto.EventResponse;
import com.sentinel.backend.dto.PageResponse;
import com.sentinel.backend.service.SecurityEventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final SecurityEventService service;
    private final DemoWriteKey writeKey;

    public EventController(SecurityEventService service, DemoWriteKey writeKey) {
        this.service = service;
        this.writeKey = writeKey;
    }

    @PostMapping
    public EventResponse ingest(
            @RequestHeader(value = "X-Demo-Ingestion-Key", required = false) String suppliedKey,
            @Valid @RequestBody EventIngestRequest request) {
        writeKey.requireValid(suppliedKey);
        return service.ingest(request);
    }

    @PostMapping("/demo-data/reset")
    public Map<String, Integer> resetSyntheticData(
            @RequestHeader(value = "X-Demo-Ingestion-Key", required = false) String suppliedKey) {
        writeKey.requireValid(suppliedKey);
        return service.resetSyntheticData();
    }

    @GetMapping
    public PageResponse<EventResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(service.listEvents(pageRequest(page, size)));
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable UUID id) {
        try {
            return service.getEvent(id);
        } catch (java.util.NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    private PageRequest pageRequest(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "page must be non-negative and size must be 1..100");
        }
        return PageRequest.of(page, size);
    }
}
