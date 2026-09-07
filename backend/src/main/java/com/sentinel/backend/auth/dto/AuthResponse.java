package com.sentinel.backend.auth.dto;

public record AuthResponse(String accessToken,String refreshToken,long expiresInSeconds,UserResponse user) {}
