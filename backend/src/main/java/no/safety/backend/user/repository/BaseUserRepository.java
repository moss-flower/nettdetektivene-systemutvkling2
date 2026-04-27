package no.safety.backend.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import no.safety.backend.user.model.User;

/**
 * Defines a base JPA repository interface for user accounts.
 * Ensures that JPA inheritence functions correctly between {@link UserRepository} and its children.
 */
@NoRepositoryBean
public interface BaseUserRepository<T extends User, ID> extends JpaRepository<T, ID> {
    Optional<T> findByPublicUuid(UUID publicUuid);

    Optional<T> findByEmail(String email);
    Optional<T> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
