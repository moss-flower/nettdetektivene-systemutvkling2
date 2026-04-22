package no.safety.backend.notepad.exception;

/**
 * Thrown when a {@link NotepadEntry} is not found in the database.
 */
public class NotepadEntryNotFoundException extends RuntimeException {
    public NotepadEntryNotFoundException(String message){
        super(message);
    }
}
