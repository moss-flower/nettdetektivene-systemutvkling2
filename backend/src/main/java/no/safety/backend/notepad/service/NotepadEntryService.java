package no.safety.backend.notepad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.exception.AccessDeniedException;
import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryReadRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.notepad.dto.NotepadEntryUpdateRequest;
import no.safety.backend.notepad.exception.NotepadEntryNotFoundException;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.repository.NotepadEntryRepository;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A service that handles create, read, update and delete operations for notepad entries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotepadEntryService {

    private final NotepadEntryRepository notepadEntryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<NotepadEntryResponse> findAll(@NonNull Long callerUserInternalId, @NonNull NotepadEntryReadRequest request) {
        log.debug("Fetching page of notepad entries for user: {}, request: {}", callerUserInternalId, request);

        User user = userRepository.findById(callerUserInternalId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));

        return notepadEntryRepository.findAllByUsers_InternalId(user.getInternalId(), request.toPageable())
                .map(NotepadEntryResponse::fromEntity);
    }

    @Transactional
    public NotepadEntryResponse create(@NonNull Long callerUserInternalId, @NonNull NotepadEntryCreateRequest request) {

        log.info("Creating new notepad entry for user: {}, request: {}", callerUserInternalId, request);

        User user = userRepository.findById(callerUserInternalId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));

        var entry = new NotepadEntry();

        entry.setGameId(request.gameId());
        entry.setDateCreated(LocalDate.now());
        entry.setTitle(request.title());
        entry.setContent(request.content());
        entry.setType(request.type());
        entry.setSource(request.source());
        entry.addUser(user);

        NotepadEntry savedNotepadEntry = notepadEntryRepository.save(entry);
        log.info("Successfully created notepad entry with UUID: {}", savedNotepadEntry.getPublicUuid());

        return NotepadEntryResponse.fromEntity(savedNotepadEntry);
    }

    @Transactional
    public NotepadEntryResponse update(@NonNull Long callerUserInternalId, @NonNull UUID notepadEntryPublicUuid, @NonNull NotepadEntryUpdateRequest request) {
        log.info("Updating notepad entry: {}, called by user: {}, with request {}", notepadEntryPublicUuid, callerUserInternalId, request);

        NotepadEntry entry = notepadEntryRepository.findByPublicUuid(notepadEntryPublicUuid)
                .orElseThrow(() -> {
                    log.debug("Resource not found: Entry UUID {}", notepadEntryPublicUuid);
                    return new NotepadEntryNotFoundException("Notepad entry not found with ID: " + notepadEntryPublicUuid);
                });

        boolean hasAccess = entry.getUsers().stream()
        .anyMatch(u -> u.getInternalId().equals(callerUserInternalId));

        if (!hasAccess) {
            log.warn("Security Violation: User ID {} attempted unauthorized access to Entry UUID {}", 
                    callerUserInternalId, notepadEntryPublicUuid);
            throw new AccessDeniedException("You do not have permission to access this entry.");
        }

        entry.setTitle(request.title());
        entry.setContent(request.content());
        entry.setType(request.type());
        entry.setSource(request.source());

        log.info("Successfully updated notepad entry: {}", notepadEntryPublicUuid);
        
        return NotepadEntryResponse.fromEntity(notepadEntryRepository.save(entry));
    }

    @Transactional
    public void delete(@NonNull Long callerUserInternalId, @NonNull UUID notepadEntryPublicUuid) {
        log.info("Deleting notepad entry: {}, called by user: {}", notepadEntryPublicUuid, callerUserInternalId);

        User user = userRepository.findById(callerUserInternalId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));

        NotepadEntry entry = notepadEntryRepository.findByPublicUuidAndUsers_InternalId(notepadEntryPublicUuid, user.getInternalId())
            .orElseThrow(() -> {
                log.warn("Security or Resource failure: User ID {} attempted to access Entry UUID {}", 
                        user.getInternalId(), notepadEntryPublicUuid);
                return new NotepadEntryNotFoundException("Notepad entry not found or access denied.");
            });

        notepadEntryRepository.delete(entry);
        log.info("Successfully deleted notepad entry: {}", notepadEntryPublicUuid);
    }
}
