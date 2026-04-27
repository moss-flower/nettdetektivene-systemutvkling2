package no.safety.backend.auth.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.safety.backend.user.model.User;

public record UserLoginResponse(
    UUID publicUuid,
    String username,
    String email,
    Set<String> roles,
    String message
) {

    /**
     * Static factory method to map the Entity to the Response DTO.
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
