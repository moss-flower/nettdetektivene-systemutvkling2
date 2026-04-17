package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {
} 
