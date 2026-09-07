package com.sentinel.backend.core.ai.dto;

public record TrainModelResponse(
        String modelVersion,
        int samples,
        String message
) {}
