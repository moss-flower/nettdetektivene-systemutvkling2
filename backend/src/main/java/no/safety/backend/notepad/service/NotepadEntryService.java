package no.safety.backend.notepad.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.auth.model.UserPrincipal;
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
 * A service that handles CRUD operations for notepad entries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotepadEntryService {

    private final NotepadEntryRepository notepadEntryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<NotepadEntryResponse> findAll(@NonNull UserPrincipal principal, @NonNull NotepadEntryReadRequest request) {
        log.debug("Fetching page of notepad entries with request: {}", request);

        User user = userRepository.getReferenceById(principal.user().getInternalId());

        return notepadEntryRepository.findAllByUsers_InternalId(user.getInternalId(), request.toPageable())
                .map(NotepadEntryResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public NotepadEntryResponse findByUuid(@NonNull UUID uuid) {
        log.debug("Fetching notepad entry with public UUID: {}", uuid);

        return notepadEntryRepository.findByPublicUuid(uuid)
                .map(NotepadEntryResponse::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Read failed: No entry found for UUID {}", uuid);
                    return new EntityNotFoundException("Notepad entry not found: " + uuid);
                });
    }

    @Transactional
    public NotepadEntryResponse create(@NonNull UserPrincipal principal, @NonNull NotepadEntryCreateRequest request) {

        log.info("Creating new notepad entry for user: {}, request: {}", principal.getUsername(), request);

        User user = userRepository.getReferenceById(principal.user().getInternalId());

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
    public NotepadEntryResponse update(@NonNull UUID uuid, @NonNull NotepadEntryUpdateRequest request) {
        log.info("Updating notepad entry: {}", uuid);

        NotepadEntry entry = notepadEntryRepository.findByPublicUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("Update failed: Entry {} not found", uuid);
                    return new EntityNotFoundException("Notepad entry not found with id: " + uuid);
                });

        entry.setTitle(request.title());
        entry.setContent(request.content());
        entry.setType(request.type());
        entry.setSource(request.source());

        log.info("Successfully updated notepad entry: {}", uuid);
        
        return NotepadEntryResponse.fromEntity(notepadEntryRepository.save(entry));
    }

    @Transactional
    public void delete(@NonNull UUID uuid) {
        log.info("Deleting notepad entry: {}", uuid);

        if (!notepadEntryRepository.existsByPublicUuid(uuid)) {
            log.warn("Delete failed: Entry {} does not exist", uuid);
            throw new EntityNotFoundException("Notepad entry not found with id: " + uuid);
        }

        notepadEntryRepository.deleteByPublicUuid(uuid);
        log.info("Successfully deleted notepad entry: {}", uuid);
    }
}
