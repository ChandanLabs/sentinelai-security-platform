package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.risk.RiskLevel;
import com.sentinel.backend.core.risk.RiskScore;
import com.sentinel.backend.core.risk.RiskScoreRepository;
import com.sentinel.backend.core.incident.dto.AlertResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AlertService {
    private final AlertRepository alerts;
    private final RiskScoreRepository riskScores;
    private final IncidentRepository incidents;

    public AlertService(AlertRepository alerts, RiskScoreRepository riskScores, IncidentRepository incidents){
        this.alerts=alerts;this.riskScores=riskScores;this.incidents=incidents;
    }

    @Transactional
    public AlertResponse generateIfRequired(Event event){
        RiskScore risk=riskScores.findByEventId(event.getId())
                .orElseThrow(()->new NoSuchElementException("Risk score not available for event"));
        if(risk.getLevel()==RiskLevel.LOW || risk.getLevel()==RiskLevel.MEDIUM) return null;
        Alert existing=alerts.findByEventId(event.getId()).orElse(null);
        if(existing!=null)return AlertResponse.from(existing);

        Alert alert=new Alert();
        alert.setEvent(event);
        alert.setTitle(risk.getLevel()+" security risk detected");
        alert.setDescription("Event " + event.getEventType() + " reached risk score " + risk.getScore() + "/100. " + risk.getExplanation());
        alert.setRiskScore(risk.getScore());
        alert.setRiskLevel(risk.getLevel());
        alert.setStatus(AlertStatus.OPEN);
        Alert saved=alerts.save(alert);

        // Critical events are immediately grouped into an incident so the demo has a clear escalation path.
        if(risk.getLevel()==RiskLevel.CRITICAL){
            Incident incident=new Incident();
            incident.setTitle("Critical security incident: " + event.getEventType());
            incident.setSummary("Automatically escalated from critical alert for event " + event.getId());
            incident.setSeverity(IncidentSeverity.CRITICAL);
            incident.setStatus(IncidentStatus.OPEN);
            incident.setCreatedBy("SYSTEM");
            Incident savedIncident=incidents.save(incident);
            saved.setIncident(savedIncident);
            saved=alerts.save(saved);
        }
        return AlertResponse.from(saved);
    }

    @Transactional(readOnly=true)
    public Page<AlertResponse> all(int page,int size){return alerts.findAllByOrderByCreatedAtDesc(pageable(page,size)).map(AlertResponse::from);}
    @Transactional(readOnly=true)
    public Page<AlertResponse> byStatus(AlertStatus status,int page,int size){return alerts.findByStatusOrderByCreatedAtDesc(status,pageable(page,size)).map(AlertResponse::from);}
    @Transactional(readOnly=true)
    public Page<AlertResponse> byRisk(RiskLevel level,int page,int size){return alerts.findByRiskLevelOrderByCreatedAtDesc(level,pageable(page,size)).map(AlertResponse::from);}
    @Transactional(readOnly=true)
    public Page<AlertResponse> byIncident(UUID incidentId,int page,int size){
        if(!incidents.existsById(incidentId))throw new NoSuchElementException("Incident not found");
        return alerts.findByIncidentIdOrderByCreatedAtDesc(incidentId,pageable(page,size)).map(AlertResponse::from);
    }
    @Transactional(readOnly=true)
    public AlertResponse one(UUID id){return AlertResponse.from(alerts.findById(id).orElseThrow(()->new NoSuchElementException("Alert not found")));}

    @Transactional
    public AlertResponse updateStatus(UUID id, AlertStatus status){
        Alert alert=alerts.findById(id).orElseThrow(()->new NoSuchElementException("Alert not found"));
        if(status==AlertStatus.OPEN){
            if(alert.getStatus()==AlertStatus.RESOLVED) throw new IllegalStateException("Resolved alert cannot be reopened in Phase 8");
            alert.setStatus(AlertStatus.OPEN);
        } else if(status==AlertStatus.ACKNOWLEDGED){
            if(alert.getStatus()==AlertStatus.RESOLVED) throw new IllegalStateException("Resolved alert cannot be acknowledged");
            alert.setStatus(AlertStatus.ACKNOWLEDGED); alert.setAcknowledgedAt(Instant.now());
        } else {
            alert.setStatus(AlertStatus.RESOLVED); alert.setResolvedAt(Instant.now());
        }
        return AlertResponse.from(alerts.save(alert));
    }

    private Pageable pageable(int page,int size){return PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100));}
}
