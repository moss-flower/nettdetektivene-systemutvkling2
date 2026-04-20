package no.safety.backend.notepad.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryReadRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.notepad.dto.NotepadEntryUpdateRequest;
import no.safety.backend.notepad.service.NotepadEntryService;

/**
 * A controller that provides API endpoints for notepad and notes.
 */
@RestController
@RequestMapping("/api/v1/notepad")
@RequiredArgsConstructor
public class NotepadController {

    private final NotepadEntryService notepadEntryService;

    @GetMapping(value = "notes")
    public Page<NotepadEntryResponse> getNotepadEntries(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid NotepadEntryReadRequest request) {
        return notepadEntryService.findAll(userPrincipal, request);
    }

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public NotepadEntryResponse createNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody NotepadEntryCreateRequest request){
        return notepadEntryService.create(userPrincipal, request);
    }

    @PutMapping(value = "update/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public NotepadEntryResponse createNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID uuid, 
            @Valid @RequestBody NotepadEntryUpdateRequest request){
        return notepadEntryService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotepadEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID uuid) {
        notepadEntryService.delete(uuid);
    }
}
