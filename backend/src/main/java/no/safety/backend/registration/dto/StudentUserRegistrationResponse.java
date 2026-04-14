package no.safety.backend.registration.dto;

import jakarta.validation.constraints.NotBlank;

public record StudentUserRegistrationResponse(
    @NotBlank String username,
    @NotBlank String password
) {
} 
