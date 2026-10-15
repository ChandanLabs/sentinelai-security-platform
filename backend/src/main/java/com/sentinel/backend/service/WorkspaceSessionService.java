package com.sentinel.backend.service;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class WorkspaceSessionService {
    public static final String USER_ID = "workspaceUserId";
    public static final String SESSION_TAG = "workspaceSessionTag";
    private static final String CSRF_TOKEN = "workspaceCsrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String csrfToken(HttpSession session) {
        Object existing = session.getAttribute(CSRF_TOKEN);
        if (existing instanceof String token) {
            return token;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN, token);
        return token;
    }

    public void requireCsrf(HttpSession session, String supplied) {
        Object stored = session.getAttribute(CSRF_TOKEN);
        if (!(stored instanceof String expected) || supplied == null
                || !MessageDigest.isEqual(
                        expected.getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CSRF token is missing or invalid.");
        }
    }

    public UUID requireOwner(HttpSession session) {
        Object value = session.getAttribute(USER_ID);
        if (!(value instanceof String userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to access the workspace.");
        }
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            session.invalidate();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Workspace session is invalid.");
        }
    }

    public String requireSessionTag(HttpSession session) {
        Object value = session.getAttribute(SESSION_TAG);
        if (!(value instanceof String tag)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to access the workspace.");
        }
        return tag;
    }

    public String authenticate(HttpServletRequest request, UUID ownerId) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        String sessionTag = UUID.randomUUID().toString();
        session.setAttribute(USER_ID, ownerId.toString());
        session.setAttribute(SESSION_TAG, sessionTag);
        return sessionTag;
    }
}
