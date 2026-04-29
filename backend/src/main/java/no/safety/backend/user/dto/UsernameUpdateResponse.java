package no.safety.backend.user.dto;

import java.util.UUID;

/**
 * An update response for a username change for a {@link User}.
 */
public record UsernameUpdateResponse(
    UUID userUid,
    String username,
    String message
) {
    public UsernameUpdateResponse(UUID userUid, String username, String message) {
        this.userUid = userUid;
        this.username = username;
        this.message = message;
    }
}