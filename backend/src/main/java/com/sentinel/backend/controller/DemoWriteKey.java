package com.sentinel.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class DemoWriteKey {
    private final String configuredKey;

    public DemoWriteKey(@Value("${DEMO_INGESTION_KEY:}") String configuredKey) {
        this.configuredKey = configuredKey;
    }

    public void requireValid(String suppliedKey) {
        if (configuredKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Demo write access is not configured");
        }
        if (suppliedKey == null || !MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                suppliedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid demo write key");
        }
    }
}
