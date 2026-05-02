package no.safety.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO record representing a user login request.
 * Contains email and password fields with validation constraints.
 */
public record UserLoginRequest(
    @NotBlank String email,
    @NotBlank String password
) {
} 
