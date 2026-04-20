package no.safety.backend.notepad.dto;

import java.time.LocalDate;
import java.util.UUID;

import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;

public record NotepadEntryResponse (
    UUID publicUuid,
    String title,
    LocalDate dateCreated,
    String source,
    NotepadEntryType type,
    String content
) {

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
