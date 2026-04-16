package no.safety.backend.registration.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Defines a {@link StudentUser} registration request to handle data specific to students.
 */
public record StudentUserRegistrationRequest(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password
) {
} 
