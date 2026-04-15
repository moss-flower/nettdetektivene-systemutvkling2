package no.safety.backend.registration.dto;

import jakarta.validation.constraints.NotBlank;

public record StudentUserRegistrationRequest(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password
) {
} 
