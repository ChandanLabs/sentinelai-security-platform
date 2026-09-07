package com.sentinel.backend.core.application;
import com.sentinel.backend.core.asset.Asset; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="applications", indexes=@Index(name="idx_applications_name",columnList="name"))
public class Application {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false,length=120) private String name;
 @Column(length=50) private String version;
 @Column(length=30) private String environment;
 @Column(nullable=false) private boolean active=true;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="asset_id",nullable=false) private Asset asset;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist void c(){Instant n=Instant.now();createdAt=n;updatedAt=n;} @PreUpdate void u(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getVersion(){return version;} public void setVersion(String v){version=v;} public String getEnvironment(){return environment;} public void setEnvironment(String v){environment=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Asset getAsset(){return asset;} public void setAsset(Asset v){asset=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
