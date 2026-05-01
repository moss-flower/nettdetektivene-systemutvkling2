package no.safety.backend.user.dto;

import java.util.UUID;

/**
 * An update response for a password change for a {@link User}.
 */
public record PasswordUpdateResponse(
    UUID userUid,
    String message
) {
    public PasswordUpdateResponse(UUID userUid, String message) {
        this.userUid = userUid;
        this.message = message;
    }
}