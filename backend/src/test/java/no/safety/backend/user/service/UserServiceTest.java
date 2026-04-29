package no.safety.backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.user.dto.UsernameUpdateResponse;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentUserRepository studentUserRepository;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("username")
                .email("test@test.com")
                .build();
    }

    @Test
    void findByPublicUuid_success() {
        UUID uuid = new UUID(0, 0);
        when(userRepository.findByPublicUuid(uuid)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByPublicUuid(uuid);

        assertThat(result.get().getUsername()).isEqualTo("username");
    }

    @Test
    void existsByUsername_success() {
        when(userRepository.existsByUsername("username")).thenReturn(true);

        Boolean bool = userService.existsByUsername("username");
        assertThat(bool).isEqualTo(true);
    }

    @Test
    void existsByEmail_success() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        Boolean bool = userService.existsByEmail("test@test.com");
        assertThat(bool).isEqualTo(true);
    }

    @Test
    void deleteByPublicUuid_success() {
        UUID uuid = new UUID(0, 0);
        when(userRepository.findByPublicUuid(uuid)).thenReturn(Optional.of(user));

        userService.deleteByPublicUuid(uuid);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteByPublicUuid_throwsErrorOnMissingUser() {
        UUID uuid = new UUID(0, 0);
        when(userRepository.findByPublicUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteByPublicUuid(uuid))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with UUID: " + uuid);
    }

    @Test
    void updateStudentUsername_success() {
        UUID uuid = UUID.randomUUID();
        StudentUser studentUser = StudentUser.builder()
                .username("student")
                .email("student@test.com")
                .build();

        String newUsername = "new username";

        when(studentUserRepository.findByPublicUuid(uuid)).thenReturn(Optional.of(studentUser));

        UsernameUpdateResponse response = userService.updateStudentUsername(uuid, newUsername);

        assertThat(response.username()).isEqualTo(newUsername);
        assertThat(response.userUid()).isEqualTo(uuid);
        assertThat(studentUser.getUsername()).isEqualTo(newUsername);

        verify(studentUserRepository).findByPublicUuid(uuid);
    }

    @Test
    void updateStudentUsername_throwsException_whenUserNotFound() {
        UUID uuid = UUID.randomUUID();
        String newUsername = "new username";
        when(studentUserRepository.findByPublicUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateStudentUsername(uuid, newUsername))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUsername_success() {
        String newUsername = "new username";

        when(userRepository.findById(user.getInternalId())).thenReturn(Optional.of(user));

        UsernameUpdateResponse response = userService.updateUsername(user.getInternalId(), newUsername);

        assertThat(response.username()).isEqualTo(newUsername);
        assertThat(response.userUid()).isEqualTo(user.getPublicUuid());
        assertThat(user.getUsername()).isEqualTo(newUsername);

        verify(userRepository).findById(user.getInternalId());
    }

    @Test
    void updateUsername_throwsException_whenUserNotFound() {
        String newUsername = "new username";
        when(userRepository.findById(user.getInternalId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUsername(user.getInternalId(), newUsername))
                .isInstanceOf(UserNotFoundException.class);
    }
}
