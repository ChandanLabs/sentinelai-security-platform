package com.sentinel.backend.core.eventsource.dto;
import com.sentinel.backend.core.eventsource.EventSourceType; import jakarta.validation.constraints.*; import java.util.UUID;
public record EventSourceRequest(@NotBlank @Size(max=120) String name,@NotNull EventSourceType type,@NotNull UUID applicationId,Boolean active){}
