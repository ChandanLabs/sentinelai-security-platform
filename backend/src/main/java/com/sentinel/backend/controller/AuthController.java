package com.sentinel.backend.controller;

import com.sentinel.backend.auth.AuthService;
import com.sentinel.backend.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
 private final AuthService auth; public AuthController(AuthService auth){this.auth=auth;}
 @PostMapping("/register") public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req){return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(req));}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest req){return auth.login(req);}
 @PostMapping("/refresh") public AuthResponse refresh(@Valid @RequestBody RefreshRequest req){return auth.refresh(req);}
 @PostMapping("/logout") public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req){auth.logout(req);return ResponseEntity.noContent().build();}
 @GetMapping("/me") public UserResponse me(Authentication authentication){return auth.me(authentication.getName());}
}
