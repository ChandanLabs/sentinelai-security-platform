package com.sentinel.backend.core.eventsource.dto;
import com.sentinel.backend.core.eventsource.EventSource; import java.time.Instant; import java.util.UUID;
public record EventSourceResponse(UUID id,String name,String type,boolean active,UUID applicationId,String applicationName,Instant createdAt,Instant updatedAt){public static EventSourceResponse from(EventSource e){return new EventSourceResponse(e.getId(),e.getName(),e.getType().name(),e.isActive(),e.getApplication().getId(),e.getApplication().getName(),e.getCreatedAt(),e.getUpdatedAt());}}
