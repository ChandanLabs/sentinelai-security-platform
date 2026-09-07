package com.sentinel.backend.core.eventsource;
import com.sentinel.backend.core.eventsource.dto.*; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/event-sources") public class EventSourceController{
 private final EventSourceService service; public EventSourceController(EventSourceService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public List<EventSourceResponse> all(){return service.all();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public EventSourceResponse one(@PathVariable UUID id){return service.one(id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public EventSourceResponse create(@Valid @RequestBody EventSourceRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public EventSourceResponse update(@PathVariable UUID id,@Valid @RequestBody EventSourceRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable UUID id){service.delete(id);}
}
