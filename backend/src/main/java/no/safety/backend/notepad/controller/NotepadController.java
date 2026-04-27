package no.safety.backend.notepad.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryReadRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.notepad.dto.NotepadEntryUpdateRequest;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.service.NotepadEntryService;

/**
 * A controller that provides API endpoints for notepad and notes.
 */
@RestController
@RequestMapping("/api/v1/notepad")
@RequiredArgsConstructor
@Tag(name = "Notepad", description = "CRUD Endpoints for notes")
public class NotepadController {

    private final NotepadEntryService notepadEntryService;

    /**
     * Fetches all {@link NotepadEntry} the {@link User} has access to. 
     * Filters based on the provided pagination values in the {@link NotepadEntryReadRequest}.
     * @param userPrincipal
     * @param request
     * @return
     */
    @Operation(
        summary = "Get user notes",
        description = "Retrieves a paginated list of notepad entries belonging to the authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved notes"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping(value = "notes")
    public Page<NotepadEntryResponse> getNotepadEntries(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid NotepadEntryReadRequest request) {
        return notepadEntryService.readCallerNotes(userPrincipal.getUserInternalId(), request);
    }

    /**
     * Fetches all {@link NotepadEntry} the {@link User} has access to. 
     * Filters based on the provided pagination values in the {@link NotepadEntryReadRequest}.
     * @param userPrincipal
     * @param request
     * @return
     */
    @Operation(
        summary = "Get student notes",
        description = "Retrieves a paginated list of notes for a specific student. Requires TEACHER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved student notes"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content)
    })
    @GetMapping(value = "/students/{studentUuid}/notes/")
    @PreAuthorize("hasRole('TEACHER')")
    public Page<NotepadEntryResponse> getNotepadEntriesForStudent(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID studentUuid, 
            @Valid NotepadEntryReadRequest request) {
        return notepadEntryService.readStudentNotes(userPrincipal.getUserInternalId(), studentUuid, request);
    }

    /**
     * Creates a new {@link NotepadEntry} for the user
     * @param userPrincipal
     * @param request
     * @return
     */
    @Operation(
        summary = "Create note",
        description = "Creates a new notepad entry for the authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Note successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public NotepadEntryResponse createNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody NotepadEntryCreateRequest request){
        return notepadEntryService.create(userPrincipal.getUserInternalId(), request);
    }

    /**
     * Updated an existing {@link NotepadEntry} based on the provided {@link UUID}.
     * @param userPrincipal
     * @param uuid
     * @param request
     * @return
     */
    @Operation(
        summary = "Update note",
        description = "Updates an existing notepad entry identified by UUID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Note successfully updated"),
        @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)
    })
    @PutMapping(value = "update/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public NotepadEntryResponse updateNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID uuid, 
            @Valid @RequestBody NotepadEntryUpdateRequest request){
        return notepadEntryService.update(userPrincipal.getUserInternalId(), uuid, request);
    }

    /**
     * Deletes the {@link NotepadEntry} matching the provided {@link UUID}.
     * @param userPrincipal
     * @param uuid
     */
    @Operation(
        summary = "Delete note",
        description = "Permanently deletes a notepad entry."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Note successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)
    })
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID uuid) {
        notepadEntryService.delete(userPrincipal.getUserInternalId(), uuid);
    }
}
