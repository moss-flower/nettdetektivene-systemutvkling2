package no.safety.backend.exception.domain;

/**
 * Thrown when an email or username is already taken.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
