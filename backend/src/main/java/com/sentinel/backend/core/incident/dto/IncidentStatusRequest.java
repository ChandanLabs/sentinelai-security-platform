package com.sentinel.backend.core.incident.dto;

import com.sentinel.backend.core.incident.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record IncidentStatusRequest(@NotNull IncidentStatus status) {}
