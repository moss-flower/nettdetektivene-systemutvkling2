package no.safety.backend.notepad.service;

import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.notepad.dto.*;
import no.safety.backend.notepad.exception.NotepadEntryNotFoundException;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.notepad.repository.NotepadEntryRepository;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
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

    @InjectMocks
    private NotepadEntryService notepadEntryService;

    private final Long USER_ID = 1L;
    private final UUID ENTRY_UUID = UUID.randomUUID();

    @Test
    void create_ShouldReturnSavedEntry_WhenUserExists() {
        var request = new NotepadEntryCreateRequest(
            100L, 
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
        var request = new NotepadEntryCreateRequest(100L, "Title", NotepadEntryType.TIP, "Content", null);
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.create(USER_ID, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void findAll_ShouldReturnPage_WhenUserExists() {
        var user = new User();
        user.setInternalId(USER_ID);
        var readRequest = mock(NotepadEntryReadRequest.class);
        given(readRequest.toPageable()).willReturn(Pageable.unpaged());

        var entry = new NotepadEntry();
        entry.setPublicUuid(ENTRY_UUID);
        Page<NotepadEntry> page = new PageImpl<>(List.of(entry));

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.findAllByUsers_InternalId(USER_ID, Pageable.unpaged())).willReturn(page);

        Page<NotepadEntryResponse> result = notepadEntryService.findAll(USER_ID, readRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).publicUuid()).isEqualTo(ENTRY_UUID);
    }

    @Test
    void update_ShouldSaveNewData_WhenUserOwnsEntry() {
        var updateRequest = new NotepadEntryUpdateRequest("New Title", NotepadEntryType.TIP, "New Content", "Source");
        var user = new User();
        user.setInternalId(USER_ID);
        
        var existingEntry = new NotepadEntry();
        existingEntry.setPublicUuid(ENTRY_UUID);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.findByPublicUuidAndUsers_InternalId(ENTRY_UUID, USER_ID))
                .willReturn(Optional.of(existingEntry));
        given(notepadEntryRepository.save(any(NotepadEntry.class))).willReturn(existingEntry);

        NotepadEntryResponse result = notepadEntryService.update(USER_ID, ENTRY_UUID, updateRequest);

        assertThat(existingEntry.getTitle()).isEqualTo("New Title");
        verify(notepadEntryRepository).save(existingEntry);
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenEntryNotFoundOrNotOwned() {
        var updateRequest = new NotepadEntryUpdateRequest("New Title", NotepadEntryType.TIP, "New Content", "Source");
        User user = new User();
        user.setInternalId(USER_ID);
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.findByPublicUuidAndUsers_InternalId(ENTRY_UUID, USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> notepadEntryService.update(USER_ID, ENTRY_UUID, updateRequest))
                .isInstanceOf(NotepadEntryNotFoundException.class);
    }

    @Test
    void delete_ShouldInvokeRepositoryDelete_WhenEntryExistsAndIsOwned() {
        var user = new User();
        user.setInternalId(USER_ID);
        var entry = new NotepadEntry();

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(notepadEntryRepository.findByPublicUuidAndUsers_InternalId(ENTRY_UUID, USER_ID))
                .willReturn(Optional.of(entry));

        notepadEntryService.delete(USER_ID, ENTRY_UUID);

        verify(notepadEntryRepository).delete(entry);
    }
}