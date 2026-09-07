package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.risk.RiskLevel;
import com.sentinel.backend.core.risk.RiskScore;
import com.sentinel.backend.core.risk.RiskScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock AlertRepository alerts;
    @Mock RiskScoreRepository riskScores;
    @Mock IncidentRepository incidents;

    @Test
    void lowRiskDoesNotCreateAlert(){
        AlertService service=new AlertService(alerts,riskScores,incidents);
        Event event=new Event();
        RiskScore risk=new RiskScore(); risk.setEvent(event); risk.setScore(20); risk.setLevel(RiskLevel.LOW);
        when(riskScores.findByEventId(any())).thenReturn(Optional.of(risk));
        assertNull(service.generateIfRequired(event));
        verify(alerts,never()).save(any());
    }

    @Test
    void criticalRiskCreatesAlertAndIncident(){
        AlertService service=new AlertService(alerts,riskScores,incidents);
        Event event=new Event(); event.setEventType("privilege_escalation");
        RiskScore risk=new RiskScore(); risk.setEvent(event); risk.setScore(92); risk.setLevel(RiskLevel.CRITICAL); risk.setExplanation("critical test");
        when(riskScores.findByEventId(any())).thenReturn(Optional.of(risk));
        when(alerts.findByEventId(any())).thenReturn(Optional.empty());
        when(alerts.save(any(Alert.class))).thenAnswer(i->i.getArgument(0));
        when(incidents.save(any(Incident.class))).thenAnswer(i->i.getArgument(0));
        var response=service.generateIfRequired(event);
        assertNotNull(response); assertEquals(RiskLevel.CRITICAL,response.riskLevel());
        verify(incidents).save(any(Incident.class));
        verify(alerts,atLeast(2)).save(any(Alert.class));
    }
}
