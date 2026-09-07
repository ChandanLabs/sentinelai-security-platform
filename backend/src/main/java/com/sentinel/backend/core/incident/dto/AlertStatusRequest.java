package com.sentinel.backend.core.incident.dto;

import com.sentinel.backend.core.incident.AlertStatus;
import jakarta.validation.constraints.NotNull;

public record AlertStatusRequest(@NotNull AlertStatus status) {}
