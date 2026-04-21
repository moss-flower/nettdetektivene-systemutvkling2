package no.safety.backend.notepad.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryReadRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.notepad.dto.NotepadEntryUpdateRequest;
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
    public Page<NotepadEntryResponse> findAll(@NonNull UUID callerUuid, @NonNull NotepadEntryReadRequest request) {
        log.debug("Fetching page of notepad entries for user: {}, request: {}", callerUuid, request);

        User user = userRepository.findByPublicUuid(callerUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with UUID: " + callerUuid));

        return notepadEntryRepository.findAllByUsers_InternalId(user.getInternalId(), request.toPageable())
                .map(NotepadEntryResponse::fromEntity);
    }

    @Transactional
    public NotepadEntryResponse create(@NonNull UUID callerUuid, @NonNull NotepadEntryCreateRequest request) {

        log.info("Creating new notepad entry for user: {}, request: {}", callerUuid, request);

        User user = userRepository.findByPublicUuid(callerUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with UUID: " + callerUuid));

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
    public NotepadEntryResponse update(@NonNull UUID callerUuid, @NonNull UUID uuid, @NonNull NotepadEntryUpdateRequest request) {
        log.info("Updating notepad entry: {}, called by user: {}, with request {}", uuid, callerUuid, request);

        User user = userRepository.findByPublicUuid(callerUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with UUID: " + callerUuid));

        NotepadEntry entry = notepadEntryRepository.findByPublicUuidAndUsers_InternalId(uuid, user.getInternalId())
            .orElseThrow(() -> {
                log.warn("Security or Resource failure: User ID {} attempted to access Entry UUID {}", 
                        user.getInternalId(), uuid);
                return new EntityNotFoundException("Notepad entry not found or access denied.");
            });

        entry.setTitle(request.title());
        entry.setContent(request.content());
        entry.setType(request.type());
        entry.setSource(request.source());

        log.info("Successfully updated notepad entry: {}", uuid);
        
        return NotepadEntryResponse.fromEntity(notepadEntryRepository.save(entry));
    }

    @Transactional
    public void delete(@NonNull UUID callerUuid, @NonNull UUID uuid) {
        log.info("Deleting notepad entry: {}, called by user: {}", uuid, callerUuid);

        User user = userRepository.findByPublicUuid(callerUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with UUID: " + callerUuid));

        NotepadEntry entry = notepadEntryRepository.findByPublicUuidAndUsers_InternalId(uuid, user.getInternalId())
            .orElseThrow(() -> {
                log.warn("Security or Resource failure: User ID {} attempted to access Entry UUID {}", 
                        user.getInternalId(), uuid);
                return new EntityNotFoundException("Notepad entry not found or access denied.");
            });

        notepadEntryRepository.delete(entry);
        log.info("Successfully deleted notepad entry: {}", uuid);
    }
}
