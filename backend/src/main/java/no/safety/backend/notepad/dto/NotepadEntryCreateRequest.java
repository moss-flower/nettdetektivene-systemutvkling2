package no.safety.backend.notepad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import org.jspecify.annotations.Nullable;

/**
 * DTO for notepad entry creation requests.
 * Contains the required fields for creating a new notepad entry.
 */
public record NotepadEntryCreateRequest(
    @NotBlank 
    @Size(max = 128) 
    String title,

    @NotNull
    NotepadEntryType type,

    @NotBlank 
    String content,

    @Size(max = 256) 
    @Nullable 
    String source
) {}
