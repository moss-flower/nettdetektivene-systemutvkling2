package no.safety.backend.registration.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.safety.backend.user.model.StudentUser;

/**
 * DTO for student user registration responses.
 * Contains the registered user details and a success message to return to the client.
 */
public record StudentUserRegistrationResponse(
    UUID publicUuid,
    String username,
    String email,
    Set<String> roles,
    String message
) {
    /**
     * Converts a {@link StudentUser} entity to a response DTO.
     *
     * @param user the student user entity to convert
     * @param message the success message to include in the response
     * @return the corresponding student registration response DTO
     */
    public static StudentUserRegistrationResponse fromEntity(StudentUser user, String message) {
        return new StudentUserRegistrationResponse(
            user.getPublicUuid(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles().stream()
                .map(role -> role.getType().name()) 
                .collect(Collectors.toUnmodifiableSet()),
            message
        );
    }
}