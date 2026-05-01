package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotNull;

public record UserLoginTokenResponse(
    @NotNull UserLoginResponse userLoginResponse,
    @NotNull String accessToken,
    @NotNull String refreshToken
) {
} 
