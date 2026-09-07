package com.sentinel.backend.core.event;

import com.sentinel.backend.core.event.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService service;
    public EventController(EventService service) { this.service = service; }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public EventResponse ingest(@Valid @RequestBody EventRequest request) { return service.ingest(request); }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<EventResponse> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        return service.all(Math.max(page, 0), Math.max(size, 1));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public EventResponse one(@PathVariable UUID id) { return service.one(id); }

    @GetMapping("/source/{sourceId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<EventResponse> bySource(@PathVariable UUID sourceId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        return service.bySource(sourceId, Math.max(page, 0), Math.max(size, 1));
    }
}
