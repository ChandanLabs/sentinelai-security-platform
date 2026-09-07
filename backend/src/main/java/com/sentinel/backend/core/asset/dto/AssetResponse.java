package com.sentinel.backend.core.asset.dto;
import com.sentinel.backend.core.asset.Asset;
import java.time.Instant; import java.util.UUID;
public record AssetResponse(UUID id,String name,String hostname,String ipAddress,String environment,boolean active,Instant createdAt,Instant updatedAt){
 public static AssetResponse from(Asset a){return new AssetResponse(a.getId(),a.getName(),a.getHostname(),a.getIpAddress(),a.getEnvironment(),a.isActive(),a.getCreatedAt(),a.getUpdatedAt());}
}
