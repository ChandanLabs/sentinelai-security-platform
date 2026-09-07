package com.sentinel.backend.core.application.dto;
import com.sentinel.backend.core.application.Application; import java.time.Instant; import java.util.UUID;
public record ApplicationResponse(UUID id,String name,String version,String environment,boolean active,UUID assetId,String assetName,Instant createdAt,Instant updatedAt){
 public static ApplicationResponse from(Application a){return new ApplicationResponse(a.getId(),a.getName(),a.getVersion(),a.getEnvironment(),a.isActive(),a.getAsset().getId(),a.getAsset().getName(),a.getCreatedAt(),a.getUpdatedAt());}
}
