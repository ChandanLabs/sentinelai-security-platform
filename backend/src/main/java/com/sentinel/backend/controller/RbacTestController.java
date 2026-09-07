package com.sentinel.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/test")
public class RbacTestController {
 @GetMapping("/admin") @PreAuthorize("hasRole('ADMIN')") public String admin(){return "ADMIN access granted";}
 @GetMapping("/analyst") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public String analyst(){return "ANALYST access granted";}
 @GetMapping("/viewer") @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')") public String viewer(){return "VIEWER access granted";}
}
