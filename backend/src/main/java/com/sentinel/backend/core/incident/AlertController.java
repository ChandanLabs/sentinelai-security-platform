package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.incident.dto.*;
import com.sentinel.backend.core.risk.RiskLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    private final AlertService service;
    public AlertController(AlertService service){this.service=service;}

    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<AlertResponse> all(@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.all(page,size);}
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public AlertResponse one(@PathVariable UUID id){return service.one(id);}
    @GetMapping("/status/{status}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<AlertResponse> byStatus(@PathVariable AlertStatus status,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.byStatus(status,page,size);}
    @GetMapping("/risk/{level}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<AlertResponse> byRisk(@PathVariable RiskLevel level,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.byRisk(level,page,size);}
    @GetMapping("/incident/{incidentId}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<AlertResponse> byIncident(@PathVariable UUID incidentId,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.byIncident(incidentId,page,size);}
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public AlertResponse updateStatus(@PathVariable UUID id,@Valid @RequestBody AlertStatusRequest request){return service.updateStatus(id,request.status());}
}
