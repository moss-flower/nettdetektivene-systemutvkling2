package no.safety.backend.registration.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.safety.backend.user.model.TeacherUser;

/**
 * DTO for teacher user registration responses.
 * Contains the registered user details and a success message to return to the client.
 */
public record TeacherUserRegistrationResponse(
    UUID publicUuid,
    String username,
    String email,
    Set<String> roles,
    String message
) {
    /**
     * Converts a {@link TeacherUser} entity to a response DTO.
     *
     * @param user the teacher user entity to convert
     * @param message the success message to include in the response
     * @return the corresponding teacher registration response DTO
     */
    public static TeacherUserRegistrationResponse fromEntity(TeacherUser user, String message) {
        return new TeacherUserRegistrationResponse(
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