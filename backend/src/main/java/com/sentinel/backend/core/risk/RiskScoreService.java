package com.sentinel.backend.core.risk;

import com.sentinel.backend.core.ai.AnomalyDetection;
import com.sentinel.backend.core.ai.AnomalyDetectionRepository;
import com.sentinel.backend.core.detection.RuleDetection;
import com.sentinel.backend.core.detection.RuleDetectionRepository;
import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventRepository;
import com.sentinel.backend.core.event.EventSeverity;
import com.sentinel.backend.core.risk.dto.RiskScoreResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class RiskScoreService {
    private final RiskScoreRepository scores;
    private final RuleDetectionRepository rules;
    private final AnomalyDetectionRepository ai;
    private final EventRepository events;

    public RiskScoreService(RiskScoreRepository scores, RuleDetectionRepository rules, AnomalyDetectionRepository ai, EventRepository events) {
        this.scores=scores; this.rules=rules; this.ai=ai; this.events=events;
    }

    @Transactional
    public RiskScoreResponse calculate(Event event) {
        RiskScore existing=scores.findByEventId(event.getId()).orElse(null);
        if(existing!=null) return RiskScoreResponse.from(existing);

        List<RuleDetection> matches=rules.findByEventIdOrderByDetectedAtDesc(event.getId());
        AnomalyDetection anomaly=ai.findByEventId(event.getId()).orElse(null);
        double ruleComponent=ruleComponent(event.getSeverity(), matches);
        boolean aiAvailable=anomaly!=null && Boolean.TRUE.equals(anomaly.getAnomaly()) && anomaly.getAnomalyScore()!=null
                || anomaly!=null && "SCORED".equals(anomaly.getStatus()) && anomaly.getAnomalyScore()!=null;
        double aiComponent=aiAvailable ? clamp(anomaly.getAnomalyScore()*100.0) : 0.0;
        double blended=aiAvailable ? (ruleComponent*0.55 + aiComponent*0.45) : ruleComponent;
        // Deterministic security severity is a floor; ML augments but cannot downgrade a confirmed critical/high signal.
        double combined=aiAvailable ? Math.max(ruleComponent, blended) : ruleComponent;
        int score=(int)Math.round(clamp(combined));
        RiskLevel level=level(score);

        RiskScore rs=new RiskScore(); rs.setEvent(event); rs.setScore(score); rs.setLevel(level);
        rs.setRuleComponent(round(ruleComponent)); rs.setAiComponent(round(aiComponent)); rs.setRuleCount(matches.size());
        rs.setAiAvailable(aiAvailable); rs.setExplanation(explanation(event, matches, anomaly, score, level));
        return RiskScoreResponse.from(scores.save(rs));
    }

    @Transactional(readOnly=true)
    public Page<RiskScoreResponse> all(int page,int size){
        return scores.findAllByOrderByCalculatedAtDesc(PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100))).map(RiskScoreResponse::from);
    }

    @Transactional(readOnly=true)
    public Page<RiskScoreResponse> byLevel(RiskLevel level,int page,int size){
        return scores.findByLevelOrderByCalculatedAtDesc(level,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100))).map(RiskScoreResponse::from);
    }

    @Transactional(readOnly=true)
    public RiskScoreResponse byEvent(UUID eventId){
        if(!events.existsById(eventId)) throw new NoSuchElementException("Event not found");
        return scores.findByEventId(eventId).map(RiskScoreResponse::from).orElseThrow(()->new NoSuchElementException("Risk score not available for event"));
    }

    private double ruleComponent(EventSeverity severity,List<RuleDetection> matches){
        double base=switch(severity){case INFO->5;case LOW->20;case MEDIUM->45;case HIGH->70;case CRITICAL->90;};
        double bonus=Math.min(matches.size()*5.0,10.0);
        if(!matches.isEmpty()){
            double maxRule=matches.stream().mapToDouble(d->severityValue(d.getSeverity())).max().orElse(0);
            base=Math.max(base,maxRule);
        }
        return Math.min(100,base+bonus);
    }
    private double severityValue(EventSeverity s){return switch(s){case INFO->5;case LOW->20;case MEDIUM->45;case HIGH->70;case CRITICAL->90;};}
    private RiskLevel level(int s){if(s>=75)return RiskLevel.CRITICAL;if(s>=50)return RiskLevel.HIGH;if(s>=25)return RiskLevel.MEDIUM;return RiskLevel.LOW;}
    private double clamp(double v){return Math.max(0,Math.min(100,v));}
    private double round(double v){return Math.round(v*100.0)/100.0;}
    private String explanation(Event e,List<RuleDetection> matches,AnomalyDetection a,int score,RiskLevel level){
        String rule=matches.isEmpty()?"No deterministic rules matched.":matches.size()+" deterministic rule(s) matched.";
        String aiText=(a!=null && a.getAnomalyScore()!=null)?"AI anomaly score="+round(a.getAnomalyScore())+".":"AI score unavailable; rule component used as the fallback.";
        return "Risk "+level+" ("+score+"/100). Event severity="+e.getSeverity()+". "+rule+" "+aiText;
    }
}
