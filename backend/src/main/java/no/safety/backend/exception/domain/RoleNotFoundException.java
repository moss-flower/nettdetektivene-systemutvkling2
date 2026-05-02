package no.safety.backend.exception.domain;

/**
 * Exception thrown when a requested role is not found in the database.
 * Typically results in an HTTP 500 Internal Server Error when handled by GlobalExceptionHandler.
 */
public class RoleNotFoundException extends RuntimeException {
    /**
     * Constructs a new RoleNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which role was not found
     */
    public RoleNotFoundException(String message) {
        super(message);
    }
}