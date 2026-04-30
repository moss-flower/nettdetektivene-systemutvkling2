package no.safety.backend.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.user.dto.UsernameUpdateResponse;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing {@link User} operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final StudentUserRepository studentUserRepository;

    /**
     * Retrieves a user by their public unique identifier.
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
     *
     * @param publicUuid the identifier of the user to remove
     * @throws RuntimeException if the user is not found
     */
    @Transactional
    public void deleteByPublicUuid(UUID publicUuid) {
        User user = userRepository
            .findByPublicUuid(publicUuid)
            .orElseThrow(() ->
                new RuntimeException("User not found with UUID: " + publicUuid)
            );

        userRepository.delete(user);
    }

    public boolean isStudent(User user) {
        for (Role role : user.getRoles()) {
            if (
                role.getType() == RoleType.ROLE_TEACHER ||
                role.getType() == RoleType.ROLE_ADMIN
            ) {
                return false;
            }
        }
        return true;
    }

    public boolean isTeacher(User user) {
        for (Role role : user.getRoles()) {
            if (role.getType() == RoleType.ROLE_TEACHER) {
                return true;
            }
        }
        return false;
    }

    public User findByInternalId(Long id) {
        return userRepository.findById(id).get();
    }

    public Optional<StudentUser> findStudentByInternalId(Long id) {
        User user = findByInternalId(id);

        if (isStudent(user)) {
            StudentUser student = (StudentUser) user;
            return Optional.of(student);
        }
        return null;
    }

    /**
     * Updates the username of a {@link StudentUser}.
     * Called by a teacher account to change a students username.
     * * @param userId The unique identifier of the user.
     *
     * @param newUsername The validated new username from the DTO.
     * @return A specialized response DTO.
     */
    @Transactional
    public UsernameUpdateResponse updateStudentUsername(
        UUID studentUserPublicUuid,
        String newUsername
    ) {
        StudentUser studentUser = studentUserRepository
            .findByPublicUuid(studentUserPublicUuid)
            .orElseThrow(() ->
                new UserNotFoundException(
                    "User not found with id: " + studentUserPublicUuid
                )
            );

        studentUser.setUsername(newUsername);

        return new UsernameUpdateResponse(
            studentUserPublicUuid,
            newUsername,
            "Username updated."
        );
    }

    /**
     * Updates the username of the calling {@link User}
     * * @param userId The unique identifier of the user.
     *
     * @param newUsername The validated new username from the DTO.
     * @return A specialized response DTO.
     */
    @Transactional
    public UsernameUpdateResponse updateUsername(
        Long callerUserInternalId,
        String newUsername
    ) {
        User user = userRepository
            .findById(callerUserInternalId)
            .orElseThrow(() ->
                new UserNotFoundException(
                    "User not found with id: " + callerUserInternalId
                )
            );

        user.setUsername(newUsername);

        return new UsernameUpdateResponse(
            user.getPublicUuid(),
            newUsername,
            "Username updated."
        );
    }
}
