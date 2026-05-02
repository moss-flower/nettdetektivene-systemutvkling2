package no.safety.backend.exception.domain;

/**
 * Exception thrown when there is an error in avatar configuration.
 * Typically occurs when avatar settings are invalid or cannot be applied.
 */
public class AvatarConfigurationException extends RuntimeException{
    /**
     * Constructs a new AvatarConfigurationException with the specified detail message.
     *
     * @param message the detail message explaining the configuration error
     */
    public AvatarConfigurationException(String message) {
        super(message);
    }
}
