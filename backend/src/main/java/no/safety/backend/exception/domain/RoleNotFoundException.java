package no.safety.backend.exception.domain;

/**
 * Thrown when a requested {@link Role} is missing from the DB.
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}