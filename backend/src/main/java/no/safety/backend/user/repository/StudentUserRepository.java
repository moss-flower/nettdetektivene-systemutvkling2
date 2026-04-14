package no.safety.backend.user.repository;

import org.springframework.stereotype.Repository;

import no.safety.backend.user.model.StudentUser;

/**
 * Defines a JPA repository for student user accounts.
 */
@Repository
public interface StudentUserRepository extends UserRepository<StudentUser, Long> {
}
