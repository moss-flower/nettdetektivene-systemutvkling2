package no.safety.backend.user.repository;

import org.springframework.stereotype.Repository;

import no.safety.backend.user.model.TeacherUser;

/**
 * Defines a JPA repository for teacher user accounts.
 */
@Repository
public interface TeacherUserRepository extends UserRepository<TeacherUser, Long> {
    
}
