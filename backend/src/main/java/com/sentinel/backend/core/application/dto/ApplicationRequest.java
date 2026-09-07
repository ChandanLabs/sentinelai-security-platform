package com.sentinel.backend.core.application.dto;
import jakarta.validation.constraints.*; import java.util.UUID;
public record ApplicationRequest(@NotBlank @Size(max=120) String name,@Size(max=50) String version,@Size(max=30) String environment,@NotNull UUID assetId,Boolean active){}
