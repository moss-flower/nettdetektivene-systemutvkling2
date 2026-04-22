package no.safety.backend.common.utils;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

    public User findUserByPublicUuid(UUID uuid) {
        return userRepository.findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
