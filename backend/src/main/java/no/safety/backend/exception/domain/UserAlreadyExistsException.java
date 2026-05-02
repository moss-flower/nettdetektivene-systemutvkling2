package no.safety.backend.exception.domain;

/**
 * Exception thrown when attempting to create a user that already exists.
 * Typically results in an HTTP 409 Conflict response when handled by GlobalExceptionHandler.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message explaining the conflict (e.g., email already registered)
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
