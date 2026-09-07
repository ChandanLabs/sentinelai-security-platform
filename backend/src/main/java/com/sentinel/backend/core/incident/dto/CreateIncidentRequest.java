package com.sentinel.backend.core.incident.dto;

import jakarta.validation.constraints.*;
import com.sentinel.backend.core.incident.IncidentSeverity;

public record CreateIncidentRequest(
        @NotBlank @Size(max=180) String title,
        @NotBlank @Size(max=2000) String summary,
        @NotNull IncidentSeverity severity,
        @NotNull java.util.UUID alertId) {}
