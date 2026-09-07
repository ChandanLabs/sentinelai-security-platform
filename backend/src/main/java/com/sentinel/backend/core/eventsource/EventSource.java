package com.sentinel.backend.core.eventsource;
import com.sentinel.backend.core.application.Application; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="event_sources", indexes=@Index(name="idx_event_sources_name",columnList="name")) public class EventSource{
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(nullable=false,length=120) private String name;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private EventSourceType type;
 @Column(nullable=false) private boolean active=true; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="application_id",nullable=false) private Application application;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist void c(){Instant n=Instant.now();createdAt=n;updatedAt=n;} @PreUpdate void u(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public EventSourceType getType(){return type;} public void setType(EventSourceType v){type=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Application getApplication(){return application;} public void setApplication(Application v){application=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
