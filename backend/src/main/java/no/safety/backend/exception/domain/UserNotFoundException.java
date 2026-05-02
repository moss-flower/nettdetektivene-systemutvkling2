package no.safety.backend.exception.domain;

/**
 * Exception thrown when a requested user is not found in the database.
 * Typically results in an HTTP 404 Not Found response when handled by GlobalExceptionHandler.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which user was not found
     */
    public UserNotFoundException(String message){
        super(message);
    }
}
