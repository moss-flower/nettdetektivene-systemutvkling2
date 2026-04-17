package no.safety.backend.exception.domain;

/**
 * Thrown when a user already exists in the database.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
