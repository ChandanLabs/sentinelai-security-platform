package com.sentinel.backend.core.event.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public record EventRequest(
        @NotNull UUID eventSourceId,
        @NotBlank @Size(max = 80) String eventType,
        @NotBlank @Pattern(regexp = "(?i)INFO|LOW|MEDIUM|HIGH|CRITICAL") String severity,
        Instant timestamp,
        @Size(max = 45) String sourceIp,
        @Size(max = 120) String username,
        @NotBlank @Size(max = 4000) String message,
        @Size(max = 12000) String metadata
) {}
