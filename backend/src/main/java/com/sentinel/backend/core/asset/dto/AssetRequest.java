package com.sentinel.backend.core.asset.dto;
import jakarta.validation.constraints.*;
public record AssetRequest(@NotBlank @Size(max=120) String name, @Size(max=255) String hostname,
                           @Size(max=45) String ipAddress, @NotBlank @Size(max=30) String environment,
                           Boolean active) {}
