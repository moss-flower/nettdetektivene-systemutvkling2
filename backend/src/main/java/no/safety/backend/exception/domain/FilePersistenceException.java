package no.safety.backend.exception.domain;

/**
 * Exception thrown when a file operation fails during persistence.
 * Typically occurs when reading from or writing to a file system fails.
 */
public class FilePersistenceException extends RuntimeException {
    /**
     * Constructs a new FilePersistenceException with the specified detail message.
     *
     * @param message the detail message explaining the file persistence error
     */
    public FilePersistenceException(String message) {
        super(message);
    }
}
