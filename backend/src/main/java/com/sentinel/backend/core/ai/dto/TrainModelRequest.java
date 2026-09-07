package com.sentinel.backend.core.ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TrainModelRequest(
        @Min(20) @Max(10000) Integer sampleSize
) {}
