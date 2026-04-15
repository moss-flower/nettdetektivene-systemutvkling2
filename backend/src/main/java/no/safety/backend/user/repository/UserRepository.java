package no.safety.backend.user.repository;

import no.safety.backend.user.model.User;

/**
 * Defines a JPA repository interface for user accounts.
 * Other account types can inherit from this repository.
 */

public interface UserRepository extends BaseUserRepository<User, Long> {
}

