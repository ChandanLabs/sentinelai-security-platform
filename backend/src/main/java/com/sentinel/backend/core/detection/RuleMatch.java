package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.event.EventSeverity;

public record RuleMatch(String code, String name, EventSeverity severity, String reason) {}
