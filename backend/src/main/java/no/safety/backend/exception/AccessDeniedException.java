package no.safety.backend.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission to access.
 * Typically results in an HTTP 403 Forbidden response when handled by GlobalExceptionHandler.
 */
public class AccessDeniedException extends RuntimeException {
    /**
     * Constructs a new AccessDeniedException with the specified detail message.
     *
     * @param message the detail message explaining why access was denied
     */
    public AccessDeniedException(String message){
        super(message);
    }
}
