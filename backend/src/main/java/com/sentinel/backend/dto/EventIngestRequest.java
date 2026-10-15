package com.sentinel.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EventIngestRequest(
        @NotBlank
        @Pattern(regexp = "[a-z][a-z0-9_]{0,63}")
        String eventType,
        @NotBlank @Size(max = 64)
        String sourceIp,
        @NotBlank @Size(max = 128)
        String username,
        @NotBlank @Size(max = 1000) @Pattern(regexp = "SIMULATED DEMO EVENT.*")
        String message) {
}
