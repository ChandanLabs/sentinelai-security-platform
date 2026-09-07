package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.incident.dto.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class IncidentService {
    private final IncidentRepository incidents;
    private final AlertRepository alerts;

    public IncidentService(IncidentRepository incidents, AlertRepository alerts){this.incidents=incidents;this.alerts=alerts;}

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request, Authentication authentication){
        Alert alert=alerts.findById(request.alertId()).orElseThrow(()->new NoSuchElementException("Alert not found"));
        if(alert.getIncident()!=null)throw new IllegalStateException("Alert is already linked to an incident");
        Incident incident=new Incident();
        incident.setTitle(request.title().trim());
        incident.setSummary(request.summary().trim());
        incident.setSeverity(request.severity());
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(authentication.getName());
        Incident saved=incidents.save(incident);
        alert.setIncident(saved);
        alerts.save(alert);
        return IncidentResponse.from(saved);
    }

    @Transactional(readOnly=true)
    public Page<IncidentResponse> all(int page,int size){return incidents.findAllByOrderByCreatedAtDesc(pageable(page,size)).map(IncidentResponse::from);}
    @Transactional(readOnly=true)
    public Page<IncidentResponse> byStatus(IncidentStatus status,int page,int size){return incidents.findByStatusOrderByCreatedAtDesc(status,pageable(page,size)).map(IncidentResponse::from);}
    @Transactional(readOnly=true)
    public IncidentResponse one(UUID id){return IncidentResponse.from(incidents.findById(id).orElseThrow(()->new NoSuchElementException("Incident not found")));}

    @Transactional
    public IncidentResponse updateStatus(UUID id, IncidentStatus status){
        Incident incident=incidents.findById(id).orElseThrow(()->new NoSuchElementException("Incident not found"));
        IncidentStatus current=incident.getStatus();
        if(current==IncidentStatus.CLOSED && status!=IncidentStatus.CLOSED)throw new IllegalStateException("Closed incident cannot be reopened");
        if(status==IncidentStatus.INVESTIGATING && current!=IncidentStatus.OPEN)throw new IllegalStateException("Only OPEN incidents can move to INVESTIGATING");
        if(status==IncidentStatus.RESOLVED && current!=IncidentStatus.INVESTIGATING)throw new IllegalStateException("Incident must be INVESTIGATING before RESOLVED");
        if(status==IncidentStatus.CLOSED && current!=IncidentStatus.RESOLVED)throw new IllegalStateException("Incident must be RESOLVED before it can be CLOSED");
        if(status==current) return IncidentResponse.from(incident);
        incident.setStatus(status);
        if(status==IncidentStatus.RESOLVED || status==IncidentStatus.CLOSED) incident.setResolvedAt(Instant.now());
        else incident.setResolvedAt(null);
        return IncidentResponse.from(incidents.save(incident));
    }

    private Pageable pageable(int page,int size){return PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100));}
}
