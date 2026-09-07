package com.sentinel.backend.core.application;
import com.sentinel.backend.core.application.dto.*; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/applications") public class ApplicationController{
 private final ApplicationService service; public ApplicationController(ApplicationService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public List<ApplicationResponse> all(){return service.all();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public ApplicationResponse one(@PathVariable UUID id){return service.one(id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public ApplicationResponse create(@Valid @RequestBody ApplicationRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public ApplicationResponse update(@PathVariable UUID id,@Valid @RequestBody ApplicationRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable UUID id){service.delete(id);}
}
