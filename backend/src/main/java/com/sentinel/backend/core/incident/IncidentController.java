package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.incident.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentService service;
    public IncidentController(IncidentService service){this.service=service;}

    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<IncidentResponse> all(@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.all(page,size);}
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public IncidentResponse one(@PathVariable UUID id){return service.one(id);}
    @GetMapping("/status/{status}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<IncidentResponse> byStatus(@PathVariable IncidentStatus status,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.byStatus(status,page,size);}
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public IncidentResponse create(@Valid @RequestBody CreateIncidentRequest request,org.springframework.security.core.Authentication authentication){return service.create(request,authentication);}
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public IncidentResponse updateStatus(@PathVariable UUID id,@Valid @RequestBody IncidentStatusRequest request){return service.updateStatus(id,request.status());}
}
