package com.crossguard.api;

import com.crossguard.model.SecurityEvent;
import com.crossguard.model.SecurityIncident;
import com.crossguard.model.UserAccount;
import com.crossguard.repo.IncidentRepository;
import com.crossguard.service.AuthService;
import com.crossguard.service.DemoSessionStore;
import com.crossguard.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Pattern SESSION_ID=Pattern.compile("^[0-9a-fA-F-]{36}$");
    private final AuthService auth; private final DemoSessionStore demos; private final EventService eventService; private final IncidentRepository incidents;
    public ApiController(AuthService auth,DemoSessionStore demos,EventService eventService,IncidentRepository incidents){this.auth=auth;this.demos=demos;this.eventService=eventService;this.incidents=incidents;}

    public record Health(String status,String service,String storage){ }
    public record RegisterRequest(@NotBlank @Size(min=2,max=80) String displayName,@NotBlank @Email String email,@NotBlank @Size(min=10,max=72) String password){}
    public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
    public record ActionRequest(@NotBlank String action,String verificationCode){}
    public record IncidentUpdate(@NotBlank String status){}

    @GetMapping("/health") public Health health(){return new Health("ok","crossguard-api","postgresql-plus-ephemeral-demo-sessions");}
    @PostMapping("/auth/register") public AuthService.AuthView register(@Valid @RequestBody RegisterRequest r){return auth.register(r.displayName(),r.email(),r.password());}
    @PostMapping("/auth/login") public AuthService.AuthView login(@Valid @RequestBody LoginRequest r){return auth.login(r.email(),r.password());}
    @GetMapping("/auth/me") public AuthService.UserView me(@RequestHeader(value="Authorization",required=false) String header){return auth.view(auth.requireUser(header));}

    @GetMapping("/demo/{sessionId}/events") public List<SecurityEvent> demoEvents(@PathVariable String sessionId){validateSession(sessionId);return demos.list(sessionId);}
    @PostMapping("/demo/{sessionId}/actions") public ResponseEntity<EventService.ActionResult> demoAction(@PathVariable String sessionId,@Valid @RequestBody ActionRequest request){validateSession(sessionId);EventService.ActionResult result=eventService.demoAction(sessionId,request.action(),request.verificationCode(),demos);return ResponseEntity.status(result.allowed()?HttpStatus.OK:HttpStatus.FORBIDDEN).body(result);}
    @PostMapping("/demo/{sessionId}/reset") public Map<String,String> resetDemo(@PathVariable String sessionId){validateSession(sessionId);demos.reset(sessionId);return Map.of("message","Demo session reset.");}

    @GetMapping("/secure/events") public List<SecurityEvent> secureEvents(@RequestHeader(value="Authorization",required=false) String header){return eventService.userEvents(auth.requireUser(header));}
    @PostMapping("/secure/actions") public ResponseEntity<EventService.ActionResult> secureAction(@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody ActionRequest request){EventService.ActionResult result=eventService.secureAction(auth.requireUser(header),request.action(),request.verificationCode());return ResponseEntity.status(result.allowed()?HttpStatus.OK:HttpStatus.FORBIDDEN).body(result);}
    @GetMapping("/secure/incidents") public List<SecurityIncident> secureIncidents(@RequestHeader(value="Authorization",required=false) String header){return incidents.findTop200ByUserIdOrderByCreatedAtDesc(auth.requireUser(header).getId());}
    @PatchMapping("/secure/incidents/{id}") public SecurityIncident updateIncident(@RequestHeader(value="Authorization",required=false) String header,@PathVariable UUID id,@Valid @RequestBody IncidentUpdate update){
        UserAccount user=auth.requireUser(header); String status=update.status().toUpperCase(Locale.ROOT);
        if(!Set.of("OPEN","INVESTIGATING","RESOLVED").contains(status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status must be OPEN, INVESTIGATING, or RESOLVED.");
        SecurityIncident incident=incidents.findByIdAndUserId(id,user.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Incident not found."));
        incident.setStatus(status); return incidents.save(incident);
    }
    private void validateSession(String sessionId){if(sessionId==null||!SESSION_ID.matcher(sessionId).matches())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid demo session identifier.");}
}
