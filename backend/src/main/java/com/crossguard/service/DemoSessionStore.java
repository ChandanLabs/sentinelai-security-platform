package com.crossguard.service;

import com.crossguard.model.SecurityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DemoSessionStore {
    private static final long TTL_SECONDS=3600;
    private final Map<String, Session> sessions=new ConcurrentHashMap<>();
    private static class Session { final List<SecurityEvent> events=new ArrayList<>(); Instant touched=Instant.now(); long denied=0; }
    public synchronized SecurityEvent record(String sessionId, String action, String resource, String outcome, String explanation) {
        Session s=sessions.computeIfAbsent(sessionId, key->new Session());
        synchronized(s) {
            s.touched=Instant.now(); if("DENIED".equals(outcome)) s.denied++;
            String severity="ALLOWED".equals(outcome)?("DEMO_REPORT_EXPORTED".equals(action)?"MEDIUM":"LOW"):(s.denied>=3?"CRITICAL":"HIGH");
            String reason=explanation;
            if(s.denied>=3 && "DENIED".equals(outcome)) reason += " Repeated denied attempts reached the demo threshold (3+), so this event is CRITICAL.";
            SecurityEvent event=new SecurityEvent(UUID.randomUUID(),null,sessionId,"demo.user",action,resource,outcome,severity,reason,Instant.now());
            s.events.add(event); return event;
        }
    }
    public List<SecurityEvent> list(String sessionId) {
        Session s=sessions.get(sessionId); if(s==null) return List.of();
        synchronized(s){s.touched=Instant.now(); return s.events.stream().sorted(Comparator.comparing(SecurityEvent::getOccurredAt).reversed()).toList();}
    }
    public synchronized void reset(String sessionId){sessions.remove(sessionId);}
    @Scheduled(fixedDelay=60000) public void expireOldSessions(){
        Instant cutoff=Instant.now().minusSeconds(TTL_SECONDS);
        sessions.entrySet().removeIf(entry->{Session s=entry.getValue(); synchronized(s){return s.touched.isBefore(cutoff);}});
    }
}
