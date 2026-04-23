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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
public class NotepadController {

    private final NotepadEntryService notepadEntryService;

    /**
     * Fetches all {@link NotepadEntry} the {@link User} has access to. 
     * Filters based on the provided pagination values in the {@link NotepadEntryReadRequest}.
     * @param userPrincipal
     * @param request
     * @return
     */
    @GetMapping(value = "notes")
    public Page<NotepadEntryResponse> getNotepadEntries(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid NotepadEntryReadRequest request) {
        return notepadEntryService.findAll(userPrincipal.getUserInternalId(), request);
    }

    /**
     * Fetches all {@link NotepadEntry} the {@link User} has access to. 
     * Filters based on the provided pagination values in the {@link NotepadEntryReadRequest}.
     * @param userPrincipal
     * @param request
     * @return
     */
    @GetMapping(value = "/students/{studentUuid}/notes/")
    @PreAuthorize("hasRole('TEACHER')")
    public Page<NotepadEntryResponse> getNotepadEntriesForStudent(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID studentUuid, 
            @Valid NotepadEntryReadRequest request) {
        return notepadEntryService.findAllStudentNotes(userPrincipal.getUserInternalId(), studentUuid, request);
    }

    /**
     * Creates a new {@link NotepadEntry} for the user
     * @param userPrincipal
     * @param request
     * @return
     */
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
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID uuid) {
        notepadEntryService.delete(userPrincipal.getUserInternalId(), uuid);
    }
}
