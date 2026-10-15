package com.sentinel.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginRateLimiter {
    private static final int MAX_FAILURES = 8;
    private static final int MAX_TRACKED_USERNAMES = 10_000;
    private final Map<String, AttemptWindow> windows = new HashMap<>();

    public synchronized void check(String username) {
        Instant now = Instant.now();
        windows.entrySet().removeIf(entry ->
                entry.getValue().lastAttempt().isBefore(now.minus(15, ChronoUnit.MINUTES)));
        AttemptWindow window = windows.get(username.toLowerCase(java.util.Locale.ROOT));
        if (window != null && window.failures() >= MAX_FAILURES
                && window.lastAttempt().isAfter(now.minus(15, ChronoUnit.MINUTES))) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "Too many sign-in attempts. Try again later.");
        }
        if (window == null && windows.size() >= MAX_TRACKED_USERNAMES) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "Sign-in service is temporarily busy.");
        }
    }

    public synchronized void failed(String username) {
        String key = username.toLowerCase(java.util.Locale.ROOT);
        AttemptWindow previous = windows.get(key);
        int failures = previous == null
                || previous.lastAttempt().isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))
                ? 1 : previous.failures() + 1;
        windows.put(key, new AttemptWindow(failures, Instant.now()));
    }

    public synchronized void succeeded(String username) {
        windows.remove(username.toLowerCase(java.util.Locale.ROOT));
    }

    private record AttemptWindow(int failures, Instant lastAttempt) {
    }
}
