package no.safety.backend.exception.domain;

public class FilePersistenceException extends RuntimeException {
    public FilePersistenceException(String message) {
        super(message);
    }
}
