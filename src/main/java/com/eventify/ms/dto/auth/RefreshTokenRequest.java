package com.eventify.ms.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest( @NotBlank(message = "Email is required") String refreshToken) {}
