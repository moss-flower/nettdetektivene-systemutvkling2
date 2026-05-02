package no.safety.backend.notepad.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;

/**
 * DTO for notepad entry responses.
 * Contains the full details of a notepad entry returned to the client.
 */
public record NotepadEntryResponse (
    UUID publicUuid,
    String title,
    ZonedDateTime dateCreated,
    String source,
    NotepadEntryType type,
    String content
) {

    /**
     * Converts a {@link NotepadEntry} entity to a response DTO.
     *
     * @param entity the notepad entry entity to convert
     * @return the corresponding response DTO
     */
    public static NotepadEntryResponse fromEntity(NotepadEntry entity) {
        return new NotepadEntryResponse(
            entity.getPublicUuid(),
            entity.getTitle(),
            entity.getDateCreated(),
            entity.getSource(),
            entity.getType(),
            entity.getContent()
        );
    }

}
