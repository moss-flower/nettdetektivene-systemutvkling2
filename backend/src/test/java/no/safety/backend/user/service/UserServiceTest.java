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

import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach 
    void setUp() {
        user = User.builder()
        .username("joe")
        .email("test@test.com")
        .build();
    }

    @Test
    void findByPublicUuid_success() {
        UUID uuid = new UUID(0, 0);
        when(userRepository.findByPublicUuid(uuid)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByPublicUuid(uuid);

        assertThat(result.get().getUsername()).isEqualTo("joe");
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
}
