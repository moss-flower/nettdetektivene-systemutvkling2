package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO record containing user login response along with access and refresh tokens.
 * Used as a response for successful authentication requests.
 */
public record UserLoginTokenResponse(
    @NotNull UserLoginResponse userLoginResponse,
    @NotNull String accessToken,
    @NotNull String refreshToken
) {
} 
