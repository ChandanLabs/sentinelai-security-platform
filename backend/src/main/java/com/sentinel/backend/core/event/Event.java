package com.sentinel.backend.core.event;

import com.sentinel.backend.core.eventsource.EventSource;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_events_timestamp", columnList = "event_timestamp"),
        @Index(name = "idx_events_type", columnList = "event_type"),
        @Index(name = "idx_events_severity", columnList = "severity"),
        @Index(name = "idx_events_source_ip", columnList = "source_ip")
})
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_source_id", nullable = false)
    private EventSource eventSource;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventSeverity severity;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @Column(length = 120)
    private String username;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column(name = "metadata_json", columnDefinition = "text")
    private String metadataJson;

    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt;

    @PrePersist
    void onCreate() {
        if (eventTimestamp == null) eventTimestamp = Instant.now();
        ingestedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public EventSource getEventSource() { return eventSource; }
    public void setEventSource(EventSource v) { eventSource = v; }
    public String getEventType() { return eventType; }
    public void setEventType(String v) { eventType = v; }
    public EventSeverity getSeverity() { return severity; }
    public void setSeverity(EventSeverity v) { severity = v; }
    public Instant getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(Instant v) { eventTimestamp = v; }
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String v) { sourceIp = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { message = v; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String v) { metadataJson = v; }
    public Instant getIngestedAt() { return ingestedAt; }
}
