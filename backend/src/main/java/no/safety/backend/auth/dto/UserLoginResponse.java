package no.safety.backend.auth.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.safety.backend.user.model.User;

/**
 * DTO record representing the user login response data.
 * Contains user details and a message for the client.
 */
public record UserLoginResponse(
    UUID publicUuid,
    String username,
    String email,
    Set<String> roles,
    String message
) {

    /**
     * Static factory method to map the User entity to the Response DTO.
     * @param user the User entity to convert
     * @param message a message to include in the response
     * @return a UserLoginResponse containing user details
     */
    public static UserLoginResponse fromEntity(User user, String message) {
        return new UserLoginResponse(
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
