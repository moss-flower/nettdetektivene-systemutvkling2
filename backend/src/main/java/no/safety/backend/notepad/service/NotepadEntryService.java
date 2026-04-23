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

        NotepadEntry entry = NotepadEntry.createFrom(request, user);
        NotepadEntry savedNotepadEntry = notepadEntryRepository.save(entry);

        log.info("Successfully created notepad entry with UUID: {}", savedNotepadEntry.getPublicUuid());

        return NotepadEntryResponse.fromEntity(savedNotepadEntry);
    }

    @Transactional
    public NotepadEntryResponse update(@NonNull Long callerUserInternalId, @NonNull UUID notepadEntryPublicUuid, @NonNull NotepadEntryUpdateRequest request) {
        log.info("Updating notepad entry: {}, called by user: {}, with request {}", notepadEntryPublicUuid, callerUserInternalId, request);

        NotepadEntry entry = getNotepadEntryFromRepositoryOrThrow(notepadEntryPublicUuid);
        
        verifyAccessOrThrow(callerUserInternalId, entry);

        entry.updateValuesFrom(request);

        log.info("Successfully updated notepad entry: {}", notepadEntryPublicUuid);
        
        return NotepadEntryResponse.fromEntity(notepadEntryRepository.save(entry));
    }

    @Transactional
    public void delete(@NonNull Long callerUserInternalId, @NonNull UUID notepadEntryPublicUuid) {
        log.info("Deleting notepad entry: {}, called by user: {}", notepadEntryPublicUuid, callerUserInternalId);

        NotepadEntry entry = getNotepadEntryFromRepositoryOrThrow(notepadEntryPublicUuid);
        
        verifyAccessOrThrow(callerUserInternalId, entry);
        
        notepadEntryRepository.delete(entry);

        log.info("Successfully deleted notepad entry: {}", notepadEntryPublicUuid);
    }

    /**
     * Tries to get the {@link NotepadEntry} matching the specified {@link UUID}, from the {@link NotepadEntryRepository}.
     * Throws {@link NotepadEntryNotFoundException} if the entry was not found in the repository.
     * @param notepadEntryPublicUuid
     * @return
     */
    private NotepadEntry getNotepadEntryFromRepositoryOrThrow(UUID notepadEntryPublicUuid){
        NotepadEntry entry = notepadEntryRepository.findByPublicUuid(notepadEntryPublicUuid)
                .orElseThrow(() -> {
                    log.debug("Resource not found: Entry UUID {}", notepadEntryPublicUuid);
                    return new NotepadEntryNotFoundException("Notepad entry not found with ID: " + notepadEntryPublicUuid);
                });
        return entry;
    }

    /**
     * Verifies that the user has access to the {@link NoteEntry}.
     * Throws {@link NotepadEntryNotFoundException} if the specified entry is null.
     * Throws {@link AccessDeniedException} if the user does not have access.
     * @param userInternalId
     * @param entry
     */
    private void verifyAccessOrThrow(Long userInternalId, NotepadEntry entry){
        if (entry == null){
            log.debug("Notepad entry is null.");
            throw new NotepadEntryNotFoundException("Notepad entry not found");
        }

        boolean hasAccess = entry.getUsers().stream()
        .anyMatch(u -> u.getInternalId().equals(userInternalId));

        if (!hasAccess) {
            log.warn("Security Violation: User ID {} attempted unauthorized access to Entry UUID {}", 
                    userInternalId, entry.getPublicUuid());
            throw new AccessDeniedException("You do not have permission to access this entry.");
        }
    }
}
