package com.sentinel.backend.core.asset;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assets", indexes = {
    @Index(name = "idx_assets_name", columnList = "name"),
    @Index(name = "idx_assets_ip_address", columnList = "ip_address")
})
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false, length=120) private String name;
    @Column(length=255) private String hostname;
    @Column(name="ip_address", length=45) private String ipAddress;
    @Column(nullable=false, length=30) private String environment;
    @Column(nullable=false) private boolean active = true;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    @PrePersist void onCreate(){ Instant now=Instant.now(); createdAt=now; updatedAt=now; }
    @PreUpdate void onUpdate(){ updatedAt=Instant.now(); }
    public UUID getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public String getHostname(){return hostname;} public void setHostname(String v){hostname=v;}
    public String getIpAddress(){return ipAddress;} public void setIpAddress(String v){ipAddress=v;}
    public String getEnvironment(){return environment;} public void setEnvironment(String v){environment=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
