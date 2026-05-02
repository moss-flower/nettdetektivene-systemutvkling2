package no.safety.backend.notepad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;

import org.jspecify.annotations.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for notepad entry update requests.
 * Contains the fields that can be updated on an existing notepad entry.
 */
public record NotepadEntryUpdateRequest(
    @NotBlank 
    @Size(max = 128) 
    @Schema(description = "The title of the entry.", maxLength = 128)
    String title,

    @NotNull 
    @Schema(description = "The type of entry.", example= "TIP, INFO, or OBSERVATION")
    NotepadEntryType type,

    @NotBlank 
    @Schema(description = "The content of the entry.")
    String content,

    @Size(max = 256) 
    @Nullable 
    @Schema(description = "The source of the entry.")
    String source
) {}
