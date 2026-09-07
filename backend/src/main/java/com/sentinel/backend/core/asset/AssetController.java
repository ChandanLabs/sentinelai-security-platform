package com.sentinel.backend.core.asset;
import com.sentinel.backend.core.asset.dto.*; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/assets")
public class AssetController {
 private final AssetService service; public AssetController(AssetService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public List<AssetResponse> all(){return service.findAll();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public AssetResponse one(@PathVariable UUID id){return service.findById(id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public AssetResponse create(@Valid @RequestBody AssetRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public AssetResponse update(@PathVariable UUID id,@Valid @RequestBody AssetRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable UUID id){service.delete(id);}
}
