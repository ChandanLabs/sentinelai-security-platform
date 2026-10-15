package com.sentinel.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

public record IncidentStatusRequest(
        @NotBlank
        @Pattern(regexp = "OPEN|INVESTIGATING|RESOLVED|CLOSED")
        String status) {
}
