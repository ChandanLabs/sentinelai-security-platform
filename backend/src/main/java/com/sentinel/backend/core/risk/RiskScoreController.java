package com.sentinel.backend.core.risk;

import com.sentinel.backend.core.risk.dto.RiskScoreResponse;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/risk-scores")
public class RiskScoreController {
    private final RiskScoreService service;
    public RiskScoreController(RiskScoreService service){this.service=service;}

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<RiskScoreResponse> all(@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.all(page,size);}

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public RiskScoreResponse byEvent(@PathVariable UUID eventId){return service.byEvent(eventId);}

    @GetMapping("/level/{level}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public Page<RiskScoreResponse> byLevel(@PathVariable RiskLevel level,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="25") @Min(1) @Max(100) int size){return service.byLevel(level,page,size);}
}
