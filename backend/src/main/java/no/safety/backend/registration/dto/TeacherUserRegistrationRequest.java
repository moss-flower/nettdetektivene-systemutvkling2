package no.safety.backend.registration.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Defines a {@link TeacherUser} registration request to handle data specific to teachers.
 */
public record TeacherUserRegistrationRequest(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password
) {
} 
