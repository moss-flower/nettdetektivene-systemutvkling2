package no.safety.backend.notepad.exception;

/**
 * Exception thrown when a notepad entry is not found.
 * Used when attempting to access or modify a non-existent entry.
 */
public class NotepadEntryNotFoundException extends RuntimeException {
    public NotepadEntryNotFoundException(String message){
        super(message);
    }
}
