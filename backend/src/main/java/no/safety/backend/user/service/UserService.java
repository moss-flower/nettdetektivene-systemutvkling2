package no.safety.backend.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

/**
 * Service class for managing general user operations.
 * Acts as the bridge between controllers and the persistence layer for all {@link User} subtypes.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    // Injecting the base repository (Spring will inject the correct implementation)
    private final UserRepository userRepository;

    /**
     * Retrieves a user by their public unique identifier.
     * Use this method for all external-facing API lookups to prevent internal ID exposure.
     *
     * @param publicUuid the {@link UUID} of the user
     * @return an {@link java.util.Optional} containing the found user
     */
    @Transactional(readOnly = true)
    public java.util.Optional<User> findByPublicUuid(UUID publicUuid) {
        return userRepository.findByPublicUuid(publicUuid);
    }

    /**
     * Retrieves a paginated list of all users.
     * Best practice for 2026: Always use {@link Pageable} to prevent memory overflow.
     *
     * @param pageable pagination and sorting information
     * @return a {@link Page} of user entities
     */
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Checks if a user exists with the given email.
     * Used for real-time validation during registration or profile updates.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a user exists with the given username.
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Deletes a user by their public UUID.
     * In a production system, consider implementing "Soft Delete" here instead of hard deletion.
     *
     * @param publicUuid the identifier of the user to remove
     * @throws RuntimeException if the user is not found
     */
    public void deleteByPublicUuid(UUID publicUuid) {
        User user = userRepository.findByPublicUuid(publicUuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + publicUuid));
        
        userRepository.delete(user);
    }
}