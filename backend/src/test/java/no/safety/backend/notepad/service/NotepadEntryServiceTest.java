package no.safety.backend.notepad.service;

import no.safety.backend.exception.AccessDeniedException;
import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.notepad.dto.*;
import no.safety.backend.notepad.exception.NotepadEntryNotFoundException;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.notepad.repository.NotepadEntryRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotepadEntryServiceTest {

    @Mock
    private NotepadEntryRepository notepadEntryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentUserRepository studentUserRepository;

    @Mock
    private TeacherUserRepository teacherUserRepository;

    @InjectMocks
    private NotepadEntryService notepadEntryService;

    private final Long USER_ID = 1L;
    private final Long STUDENT_USER_ID = 2L;
    private final Long TEACHER_USER_ID = 3L;
    private final UUID ENTRY_UUID = UUID.randomUUID();
    private final UUID STUDENT_USER_UUID = UUID.randomUUID();

    @Test
    void create_ShouldReturnSavedEntry_WhenUserExists() {
        var request = new NotepadEntryCreateRequest(
            "Test Title", 
            NotepadEntryType.TIP, 
            "Test Content", 
            "Test Source"
        );
        
        var user = new User();
        user.setInternalId(USER_ID);

        var savedEntry = new NotepadEntry();
        savedEntry.setPublicUuid(ENTRY_UUID);
        savedEntry.setTitle(request.title());

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.save(any(NotepadEntry.class))).willReturn(savedEntry);

        NotepadEntryResponse result = notepadEntryService.create(USER_ID, request);

        assertThat(result).isNotNull();
        assertThat(result.publicUuid()).isEqualTo(ENTRY_UUID);
        verify(notepadEntryRepository, times(1)).save(any(NotepadEntry.class));
    }

    @Test
    void create_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        var request = new NotepadEntryCreateRequest("Title", NotepadEntryType.TIP, "Content", null);
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.create(USER_ID, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void readCallerNotes_ShouldReturnPage_WhenUserExists() {
        var user = new User();
        user.setInternalId(USER_ID);
        var readRequest = mock(NotepadEntryReadRequest.class);
        given(readRequest.toPageable()).willReturn(Pageable.unpaged());

        var entry = new NotepadEntry();
        entry.setPublicUuid(ENTRY_UUID);
        Page<NotepadEntry> page = new PageImpl<>(List.of(entry));

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.findAllByUsers_InternalId(USER_ID, Pageable.unpaged())).willReturn(page);

        Page<NotepadEntryResponse> result = notepadEntryService.readCallerNotes(USER_ID, readRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).publicUuid()).isEqualTo(ENTRY_UUID);
    }

    @Test
    void readStudentNotes_ShouldReturnPage_WhenStudentExistAndCallerIsTeacherAndHasAccess() {
        var teacherUser = new TeacherUser();
        teacherUser.setInternalId(TEACHER_USER_ID);
        var studentUser = new StudentUser();
        studentUser.setInternalId(STUDENT_USER_ID);
        studentUser.setPublicUuid(STUDENT_USER_UUID);

        var readRequest = mock(NotepadEntryReadRequest.class);
        given(readRequest.toPageable()).willReturn(Pageable.unpaged());

        var entry = new NotepadEntry();
        entry.setPublicUuid(ENTRY_UUID);
        Page<NotepadEntry> page = new PageImpl<>(List.of(entry));

        given(teacherUserRepository.findById(TEACHER_USER_ID)).willReturn(Optional.of(teacherUser));
        given(studentUserRepository.findByPublicUuid(STUDENT_USER_UUID)).willReturn(Optional.of(studentUser));
        given(notepadEntryRepository.findAllByUsers_InternalId(STUDENT_USER_ID, Pageable.unpaged())).willReturn(page);

        Page<NotepadEntryResponse> result = notepadEntryService.readStudentNotes(TEACHER_USER_ID, STUDENT_USER_UUID, readRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).publicUuid()).isEqualTo(ENTRY_UUID);
    }

    @Test
    void readStudentNotes_ShouldThrowUserNotFoundException_WhenTeacherDoesNotExist() {
        NotepadEntryReadRequest readRequest = mock(NotepadEntryReadRequest.class);

        given(teacherUserRepository.findById(TEACHER_USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.readStudentNotes(TEACHER_USER_ID, STUDENT_USER_UUID, readRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Teacher user not found.");
    }

    @Test
    void readStudentNotes_ShouldThrowUserNotFoundException_WhenStudentDoesNotExist() {
        TeacherUser teacherUser = new TeacherUser();
        teacherUser.setInternalId(TEACHER_USER_ID);
        NotepadEntryReadRequest readRequest = mock(NotepadEntryReadRequest.class);

        given(teacherUserRepository.findById(TEACHER_USER_ID))
                .willReturn(Optional.of(teacherUser));
        given(studentUserRepository.findByPublicUuid(STUDENT_USER_UUID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.readStudentNotes(TEACHER_USER_ID, STUDENT_USER_UUID, readRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Student user not found.");
    }

    @Test
    void readStudentNotes_ShouldReturnEmptyPage_WhenStudentHasNoNotes() {
        TeacherUser teacherUser = new TeacherUser();
        teacherUser.setInternalId(TEACHER_USER_ID);
        
        StudentUser studentUser = new StudentUser();
        studentUser.setInternalId(STUDENT_USER_ID);
        studentUser.setPublicUuid(STUDENT_USER_UUID);

        NotepadEntryReadRequest readRequest = mock(NotepadEntryReadRequest.class);
        Pageable pageable = PageRequest.of(0, 10);
        given(readRequest.toPageable()).willReturn(pageable);

        given(teacherUserRepository.findById(TEACHER_USER_ID))
                .willReturn(Optional.of(teacherUser));
        given(studentUserRepository.findByPublicUuid(STUDENT_USER_UUID))
                .willReturn(Optional.of(studentUser));
        given(notepadEntryRepository.findAllByUsers_InternalId(STUDENT_USER_ID, pageable))
                .willReturn(Page.empty());

        Page<NotepadEntryResponse> result = notepadEntryService.readStudentNotes(TEACHER_USER_ID, STUDENT_USER_UUID, readRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void update_ShouldSaveNewData_WhenUserOwnsEntry() {
        var updateRequest = new NotepadEntryUpdateRequest("New Title", NotepadEntryType.TIP, "New Content", "Source");
        var user = new User();
        user.setInternalId(USER_ID);
        
        var existingEntry = new NotepadEntry();
        existingEntry.setPublicUuid(ENTRY_UUID);
        existingEntry.addUser(user);

        given(notepadEntryRepository.findByPublicUuid(ENTRY_UUID))
                .willReturn(Optional.of(existingEntry));
        given(notepadEntryRepository.save(any(NotepadEntry.class))).willReturn(existingEntry);

        NotepadEntryResponse result = notepadEntryService.update(USER_ID, ENTRY_UUID, updateRequest);

        assertThat(existingEntry.getTitle()).isEqualTo("New Title");
        verify(notepadEntryRepository).save(existingEntry);
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenEntryDoesNotExist() {
        var updateRequest = new NotepadEntryUpdateRequest("New Title", NotepadEntryType.TIP, "New Content", "Source");
        User user = new User();
        user.setInternalId(USER_ID);
        given(notepadEntryRepository.findByPublicUuid(ENTRY_UUID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.update(USER_ID, ENTRY_UUID, updateRequest))
                .isInstanceOf(NotepadEntryNotFoundException.class);
    }

    @Test
    void update_ShouldThrowAccessDeniedException_WhenUserDoesNotHaveAccessToEntry() {
        var updateRequest = new NotepadEntryUpdateRequest("New Title", NotepadEntryType.TIP, "New Content", "Source");
        NotepadEntry entry = new NotepadEntry();
        User user = new User();
        user.setInternalId(USER_ID);
        given(notepadEntryRepository.findByPublicUuid(ENTRY_UUID))
                .willReturn(Optional.of(entry));

        entry.setUsers(Set.of());

        assertThatThrownBy(() -> notepadEntryService.update(USER_ID, ENTRY_UUID, updateRequest))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
void delete_ShouldThrowEntityNotFoundException_WhenEntryDoesNotExist() {
    given(notepadEntryRepository.findByPublicUuid(ENTRY_UUID))
            .willReturn(Optional.empty());

    assertThatThrownBy(() -> notepadEntryService.delete(USER_ID, ENTRY_UUID))
            .isInstanceOf(NotepadEntryNotFoundException.class);
}

@Test
void delete_ShouldThrowAccessDeniedException_WhenUserDoesNotHaveAccessToEntry() {
    NotepadEntry entry = new NotepadEntry();
    entry.setPublicUuid(ENTRY_UUID);
    entry.setUsers(Set.of());

    given(notepadEntryRepository.findByPublicUuid(ENTRY_UUID))
            .willReturn(Optional.of(entry));

    assertThatThrownBy(() -> notepadEntryService.delete(USER_ID, ENTRY_UUID))
            .isInstanceOf(AccessDeniedException.class);
}
}