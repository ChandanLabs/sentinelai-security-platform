package com.sentinel.backend.controller;

import com.sentinel.backend.dto.AuthRequest;
import com.sentinel.backend.dto.AuthResponse;
import com.sentinel.backend.model.WorkspaceUser;
import com.sentinel.backend.repository.WorkspaceUserRepository;
import com.sentinel.backend.service.WorkspaceAuthService;
import com.sentinel.backend.service.WorkspaceSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final WorkspaceAuthService authService;
    private final WorkspaceSessionService sessionService;
    private final WorkspaceUserRepository userRepository;

    public AuthController(
            WorkspaceAuthService authService,
            WorkspaceSessionService sessionService,
            WorkspaceUserRepository userRepository) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody AuthRequest request, HttpServletRequest servletRequest,
            HttpSession session, @RequestHeader("X-CSRF-Token") String csrfToken) {
        sessionService.requireCsrf(session, csrfToken);
        WorkspaceUser user = authService.register(request);
        String sessionTag = sessionService.authenticate(servletRequest, user.getId());
        authService.recordRegistration(user, "not-collected", sessionTag);
        return response(user);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody AuthRequest request, HttpServletRequest servletRequest,
            HttpSession session, @RequestHeader("X-CSRF-Token") String csrfToken) {
        sessionService.requireCsrf(session, csrfToken);
        WorkspaceUser user = authService.authenticate(request, "not-collected");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }
        String sessionTag = sessionService.authenticate(servletRequest, user.getId());
        authService.recordSuccessfulLogin(user, "not-collected", sessionTag);
        return response(user);
    }

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(HttpServletRequest request) {
        return java.util.Map.of(
                "token", sessionService.csrfToken(request.getSession(true)));
    }

    @GetMapping("/me")
    public AuthResponse me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to access the workspace.");
        }
        UUID ownerId = sessionService.requireOwner(session);
        WorkspaceUser user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Workspace account no longer exists."));
        return response(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @RequestHeader("X-CSRF-Token") String csrfToken) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to access the workspace.");
        }
        sessionService.requireCsrf(session, csrfToken);
        UUID ownerId = sessionService.requireOwner(session);
        authService.recordLogout(ownerId, "not-collected", sessionService.requireSessionTag(session));
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(
            HttpServletRequest request,
            @RequestHeader("X-CSRF-Token") String csrfToken) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to access the workspace.");
        }
        sessionService.requireCsrf(session, csrfToken);
        UUID ownerId = sessionService.requireOwner(session);
        authService.deleteAccount(ownerId);
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    private static AuthResponse response(WorkspaceUser user) {
        return new AuthResponse(user.getId(), user.getUsername());
    }
}
