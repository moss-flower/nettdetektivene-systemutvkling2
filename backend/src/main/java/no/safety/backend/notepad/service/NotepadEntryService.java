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
 * Service for managing notepad entries.
 * Provides CRUD operations and access control for notepad entries belonging to users.
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
     * Retrieves a paginated list of notepad entries for the calling user.
     *
     * @param callerUserInternalId the internal ID of the authenticated user
     * @param request the read request containing pagination parameters
     * @return a page of notepad entry responses belonging to the user
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
     * Retrieves a paginated list of notepad entries for a specific student.
     * Only a teacher with access to the student's notes can use this method.
     *
     * @param callerUserInternalId the internal ID of the authenticated teacher
     * @param studentUserUuid the UUID of the student whose notes to retrieve
     * @param request the read request containing pagination parameters
     * @return a page of notepad entry responses for the specified student
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
     * Creates a new notepad entry assigned to the calling user.
     *
     * @param callerUserInternalId the internal ID of the authenticated user
     * @param request the create request containing entry details
     * @return the created notepad entry response
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
     * Updates an existing notepad entry identified by its UUID.
     * The caller must have access to the notepad entry to perform this operation.
     *
     * @param callerUserInternalId the internal ID of the authenticated user
     * @param notepadEntryPublicUuid the public UUID of the notepad entry to update
     * @param request the update request containing new entry details
     * @return the updated notepad entry response
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
     * Deletes a notepad entry identified by its UUID.
     * The caller must be the owner of the notepad entry to perform this operation.
     *
     * @param callerUserInternalId the internal ID of the authenticated user
     * @param notepadEntryPublicUuid the public UUID of the notepad entry to delete
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
     * Retrieves a notepad entry by its public UUID from the repository.
     * Throws {@link NotepadEntryNotFoundException} if the entry is not found.
     *
     * @param notepadEntryPublicUuid the public UUID of the notepad entry
     * @return the found notepad entry
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
     * Verifies that the user has access to the specified notepad entry.
     * Throws {@link NotepadEntryNotFoundException} if the entry is null.
     * Throws {@link AccessDeniedException} if the user does not have access.
     *
     * @param userInternalId the internal ID of the user to check access for
     * @param entry the notepad entry to check access against
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
