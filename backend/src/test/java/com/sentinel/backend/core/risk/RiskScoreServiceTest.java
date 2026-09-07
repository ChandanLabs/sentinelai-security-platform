package com.sentinel.backend.core.risk;

import com.sentinel.backend.core.ai.*;
import com.sentinel.backend.core.detection.*;
import com.sentinel.backend.core.event.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskScoreServiceTest {
    @Mock RiskScoreRepository scores; @Mock RuleDetectionRepository rules; @Mock AnomalyDetectionRepository ai; @Mock EventRepository events;
    @Test void criticalEventWithoutAiFallsBackToRuleScore(){
        MockitoAnnotations.openMocks(this); RiskScoreService s=new RiskScoreService(scores,rules,ai,events);
        Event e=mock(Event.class);
        UUID id=UUID.randomUUID(); when(e.getId()).thenReturn(id); when(e.getSeverity()).thenReturn(EventSeverity.CRITICAL); when(e.getEventType()).thenReturn("auth"); when(e.getMessage()).thenReturn("critical"); when(scores.findByEventId(id)).thenReturn(Optional.empty()); when(rules.findByEventIdOrderByDetectedAtDesc(id)).thenReturn(List.of()); when(ai.findByEventId(id)).thenReturn(Optional.empty());
        RiskScore saved=new RiskScore(); saved.setEvent(e); saved.setScore(90); saved.setLevel(RiskLevel.CRITICAL); saved.setRuleComponent(90d); saved.setAiComponent(0d); saved.setRuleCount(0); saved.setAiAvailable(false); saved.setExplanation("fallback"); when(scores.save(any())).thenReturn(saved);
        assertEquals(90,s.calculate(e).score()); verify(scores).save(any(RiskScore.class));
    }
}
