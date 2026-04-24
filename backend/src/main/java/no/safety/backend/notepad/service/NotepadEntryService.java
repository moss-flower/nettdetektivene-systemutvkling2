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
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
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
    private final StudentUserRepository studentUserRepository;
    private final TeacherUserRepository teacherUserRepository;

    /**
     * Reads the notes of the calling {@link User}.
     * @param callerUserInternalId
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public Page<NotepadEntryResponse> readCallerNotes(@NonNull Long callerUserInternalId, @NonNull NotepadEntryReadRequest request) {
        log.info("Fetching page of notepad entries for user: {}, request: {}", callerUserInternalId, request);

        User user = userRepository.findById(callerUserInternalId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));

        return notepadEntryRepository.findAllByUsers_InternalId(user.getInternalId(), request.toPageable())
                .map(NotepadEntryResponse::fromEntity);
    }

    /**
     * Reads the notes of the specified {@link StudentUser} based on {@link UUID}.
     * Only a {@link TeacherUser} with access to the {@link StudentUser}'s notes can use this method.
     * @param callerUserInternalId
     * @param studentUserUuid
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public Page<NotepadEntryResponse> readStudentNotes(@NonNull Long callerUserInternalId, @NonNull UUID studentUserUuid, @NonNull NotepadEntryReadRequest request) {
        log.info("Fetching page of notepad entries for user: {}, request: {}", callerUserInternalId, request);

        TeacherUser teacherUser = teacherUserRepository.findById(callerUserInternalId)
            .orElseThrow(() -> new UserNotFoundException("Teacher user not found."));

        StudentUser studentUser = studentUserRepository.findByPublicUuid(studentUserUuid)
            .orElseThrow(() -> new UserNotFoundException("Student user not found."));
        
        //TODO: Verify that the teacher has read access to this student's notes. 

        return notepadEntryRepository.findAllByUsers_InternalId(studentUser.getInternalId(), request.toPageable())
                .map(NotepadEntryResponse::fromEntity);
    }

    /**
     * Creates a new {@link NotepadEntry} that's assigned to the calling {@link User}.
     * @param callerUserInternalId
     * @param request
     * @return
     */
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

    /**
     * Updates the internal values of the {@link NotepadEntry} tied to the given {@link UUID}.
     * This can only be done if the caller {@link User} has access to the {@link NotepadEntry}.
     * @param callerUserInternalId
     * @param notepadEntryPublicUuid
     * @param request
     * @return
     */
    @Transactional
    public NotepadEntryResponse update(@NonNull Long callerUserInternalId, @NonNull UUID notepadEntryPublicUuid, @NonNull NotepadEntryUpdateRequest request) {
        log.info("Updating notepad entry: {}, called by user: {}, with request {}", notepadEntryPublicUuid, callerUserInternalId, request);

        NotepadEntry entry = getNotepadEntryFromRepositoryOrThrow(notepadEntryPublicUuid);
        
        verifyAccessOrThrow(callerUserInternalId, entry);

        entry.updateValuesFrom(request);

        log.info("Successfully updated notepad entry: {}", notepadEntryPublicUuid);
        
        return NotepadEntryResponse.fromEntity(notepadEntryRepository.save(entry));
    }

    /**
     * Updates the {@link NotepadEntry} tied to the given {@link UUID}.
     * Can only be done if the caller {@link User} is the owner of the {@link NotepadEntry}.
     * @param callerUserInternalId
     * @param notepadEntryPublicUuid
     */
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
