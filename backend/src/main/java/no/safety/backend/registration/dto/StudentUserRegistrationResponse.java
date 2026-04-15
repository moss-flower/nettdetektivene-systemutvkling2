package no.safety.backend.registration.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.safety.backend.user.model.StudentUser;

/**
 * Defines a {@link StudentUser} reponse DTO for delivering user information to client.
 */
public record StudentUserRegistrationResponse(
    UUID publicUuid,
    String username,
    String email,
    Set<String> roles,
    String message
) {
    /**
     * Static factory method to map the Entity to the Response DTO.
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