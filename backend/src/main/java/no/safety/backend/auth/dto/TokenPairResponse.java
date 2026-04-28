package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotNull;

public record TokenPairResponse(
    @NotNull String accessToken,
    @NotNull String refreshToken
) {
}
