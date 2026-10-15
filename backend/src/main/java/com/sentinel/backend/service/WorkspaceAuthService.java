package com.sentinel.backend.service;

import com.sentinel.backend.dto.AuthRequest;
import com.sentinel.backend.model.WorkspaceDocument;
import com.sentinel.backend.model.WorkspaceUser;
import com.sentinel.backend.repository.WorkspaceDocumentRepository;
import com.sentinel.backend.repository.WorkspaceUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceAuthService {
    private final WorkspaceUserRepository userRepository;
    private final WorkspaceDocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityEventService eventService;
    private final LoginRateLimiter rateLimiter;
    private final String dummyPasswordHash;

    public WorkspaceAuthService(
            WorkspaceUserRepository userRepository,
            WorkspaceDocumentRepository documentRepository,
            PasswordEncoder passwordEncoder,
            SecurityEventService eventService,
            LoginRateLimiter rateLimiter) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
        this.rateLimiter = rateLimiter;
        this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional
    public WorkspaceUser register(AuthRequest request) {
        String username = normalize(request.username());
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is unavailable.");
        }
        UUID userId = UUID.randomUUID();
        WorkspaceUser user = userRepository.save(new WorkspaceUser(
                userId, username, passwordEncoder.encode(request.password()), Instant.now()));
        documentRepository.saveAll(List.of(
                new WorkspaceDocument(UUID.randomUUID(), userId, "Workspace guide",
                        "This sample document belongs to your account. Viewing it records a server-side access event."),
                new WorkspaceDocument(UUID.randomUUID(), userId, "Incident response notes",
                        "Use this synthetic note to explore authenticated document access and event history."),
                new WorkspaceDocument(UUID.randomUUID(), userId, "Exportable activity summary",
                        "Only safe sample content is stored in this public demonstration workspace.")));
        return user;
    }

    @Transactional
    public WorkspaceUser authenticate(AuthRequest request, String sourceIp) {
        String username = normalize(request.username());
        rateLimiter.check(username);
        WorkspaceUser user = userRepository.findByUsername(username).orElse(null);
        boolean passwordMatches = passwordEncoder.matches(
                request.password(), user == null ? dummyPasswordHash : user.getPasswordHash());
        if (user == null || !passwordMatches) {
            eventService.recordLoginFailure(
                    user == null ? null : user.getId(), sourceIp,
                    user == null ? "unknown" : actorId(user.getId()));
            rateLimiter.failed(username);
            return null;
        }
        rateLimiter.succeeded(username);
        return user;
    }

    public void recordSuccessfulLogin(WorkspaceUser user, String sourceIp, String sessionTag) {
        eventService.recordActivity(user.getId(), "login_success", sourceIp,
                actorId(user.getId()), sessionTag, "/auth/login", "ALLOWED");
    }

    public void recordRegistration(WorkspaceUser user, String sourceIp, String sessionTag) {
        eventService.recordActivity(user.getId(), "account_registered", sourceIp,
                actorId(user.getId()), sessionTag, "/auth/register", "ALLOWED");
    }

    public void recordLogout(UUID ownerId, String sourceIp, String sessionTag) {
        eventService.recordActivity(ownerId, "logout", sourceIp,
                actorId(ownerId), sessionTag, "/auth/logout", "ALLOWED");
    }

    @Transactional
    public void deleteAccount(UUID ownerId) {
        documentRepository.deleteAllByOwnerId(ownerId);
        eventService.deleteOwnerData(ownerId);
        userRepository.deleteById(ownerId);
    }

    public static String actorId(UUID ownerId) {
        return "user-" + ownerId.toString().substring(0, 8);
    }

    private static String normalize(String username) {
        return username.toLowerCase(java.util.Locale.ROOT);
    }
}
