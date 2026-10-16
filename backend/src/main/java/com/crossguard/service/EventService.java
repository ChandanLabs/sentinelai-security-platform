package com.crossguard.service;

import com.crossguard.model.SecurityEvent;
import com.crossguard.model.SecurityIncident;
import com.crossguard.model.UserAccount;
import com.crossguard.repo.IncidentRepository;
import com.crossguard.repo.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class EventService {
    private final SecurityEventRepository events; private final IncidentRepository incidents;
    @Value("${crossguard.demo-access-code}") private String demoAccessCode;
    public EventService(SecurityEventRepository events, IncidentRepository incidents){this.events=events;this.incidents=incidents;}
    public record ActionResult(boolean allowed, String message, SecurityEvent event) {}
    public ActionResult demoAction(String sessionId, String requestedAction, String verificationCode, DemoSessionStore store) {
        Action a=parse(requestedAction);
        Decision d=decide(a,verificationCode);
        SecurityEvent event=store.record(sessionId,a.name(),d.resource,d.allowed?"ALLOWED":"DENIED",d.explanation);
        return new ActionResult(d.allowed,d.message,event);
    }
    public ActionResult secureAction(UserAccount user, String requestedAction, String verificationCode) {
        Action a=parse(requestedAction); Decision d=decide(a,verificationCode);
        Instant since=Instant.now().minusSeconds(3600);
        long deniedBefore=events.countByUserIdAndOutcomeAndOccurredAtAfter(user.getId(),"DENIED",since);
        String severity=d.allowed?(a==Action.EXPORT_REPORT?"MEDIUM":"LOW"):(deniedBefore>=2?"CRITICAL":"HIGH");
        String explanation=d.explanation;
        if(!d.allowed && deniedBefore>=2) explanation += " Multiple denied attempts in the last hour crossed the rule threshold; this may indicate attempted privilege escalation.";
        SecurityEvent event=events.save(new SecurityEvent(UUID.randomUUID(),user.getId(),null,user.getEmail(),a.name(),d.resource,d.allowed?"ALLOWED":"DENIED",severity,explanation,Instant.now()));
        if("HIGH".equals(severity) || "CRITICAL".equals(severity)) {
            incidents.save(new SecurityIncident(user.getId(),event.getId(),titleFor(a),severity,explanation));
        }
        return new ActionResult(d.allowed,d.message,event);
    }
    public List<SecurityEvent> userEvents(UserAccount user){return events.findTop500ByUserIdOrderByOccurredAtDesc(user.getId());}
    private enum Action { OPEN_OVERVIEW, OPEN_DOCUMENT, EXPORT_REPORT, ACCESS_RESTRICTED_RESOURCE, RESTRICTED_OPERATION, OPEN_SECURE_AREA, OPEN_ADMIN_PANEL, TRY_API_TAMPERING }
    private record Decision(boolean allowed,String message,String resource,String explanation){}
    private Action parse(String value){
        try{return Action.valueOf(value==null?"":value.trim().toUpperCase(Locale.ROOT));}
        catch(Exception ex){throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Unknown demo action.");}
    }
    private Decision decide(Action action,String code){
        return switch(action){
            case OPEN_OVERVIEW -> new Decision(true,"Authorized account overview opened. Activity recorded.","Demo account overview","The signed-in demo identity opened an allowed account overview.");
            case OPEN_DOCUMENT -> new Decision(true,"Sample statement opened. Activity recorded.","Sample account statement","The user opened an available sample document in the authorized workspace.");
            case EXPORT_REPORT -> new Decision(true,"Simulated report export recorded. No real financial records were exported.","Demo activity report","A simulated report export action ran; no real financial records were exported.");
            case OPEN_SECURE_AREA -> {
                if(code!=null && !code.isBlank() && code.equals(demoAccessCode)) yield new Decision(true,"Step-up demo code accepted. Restricted demo view opened.","Step-up verified demo area","The server validated the configured demo verification code before allowing access. This is illustrative step-up verification, not real MFA.");
                yield new Decision(false,"Access denied. A valid verification code is required.","Step-up verified demo area","The server rejected access because the verification code was missing or incorrect.");
            }
            case ACCESS_RESTRICTED_RESOURCE -> new Decision(false,"Access denied. You do not have permission for this resource.","Confidential risk report","The current demo identity has no permission for this restricted resource. The backend denied the request.");
            case RESTRICTED_OPERATION -> new Decision(false,"Operation blocked. No account permissions were changed.","Change account permissions","This operation requires elevated authorization. The backend rejected it; no permissions were changed.");
            case OPEN_ADMIN_PANEL -> new Decision(false,"Access denied. Admin privileges are required.","Administration console","The current identity is not an administrator. The backend denied access to the protected route.");
            case TRY_API_TAMPERING -> new Decision(false,"Request rejected by server-side authorization. The UI cannot grant access.","Protected API endpoint","A simulated direct/API-tampering request reached the server without sufficient authorization and was denied. No protected data was returned.");
        };
    }
    private String titleFor(Action a){return switch(a){case ACCESS_RESTRICTED_RESOURCE->"Denied access to restricted resource";case RESTRICTED_OPERATION->"Restricted operation denied";case OPEN_SECURE_AREA->"Step-up verification failed";case OPEN_ADMIN_PANEL->"Unauthorized admin-console attempt";case TRY_API_TAMPERING->"Protected API access attempt";default->"Security event requiring review";};}
}
