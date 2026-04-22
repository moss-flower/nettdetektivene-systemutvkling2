package no.safety.backend.exception.domain;

/**
 * Thrown when a {@link User} is not found in the database.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message){
        super(message);
    }
}
