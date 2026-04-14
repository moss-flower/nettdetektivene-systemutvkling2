package no.safety.backend.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import no.safety.backend.user.model.User;

/**
 * Defines a JPA repository interface for user accounts.
 * Other account types can inherit from this repository.
 */
@NoRepositoryBean
public interface UserRepository<T extends User, ID> extends JpaRepository<T, ID> {
    Optional<User> findByPublicUuid(UUID publicUuid);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

