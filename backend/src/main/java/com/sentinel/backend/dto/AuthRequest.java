package com.sentinel.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_.-]{3,32}") String username,
        @NotBlank @Size(min = 12, max = 72) @Pattern(regexp = "[!-~]{12,72}") String password) {
}
