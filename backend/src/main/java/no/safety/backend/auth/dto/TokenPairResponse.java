package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO record representing a pair of JWT tokens (access and refresh).
 * Used when refreshing authentication sessions.
 */
public record TokenPairResponse(
    @NotNull String accessToken,
    @NotNull String refreshToken
) {
}
