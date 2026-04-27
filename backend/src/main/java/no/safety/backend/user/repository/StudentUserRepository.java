package no.safety.backend.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import no.safety.backend.user.model.StudentUser;

/**
 * Defines a JPA repository for student user accounts.
 */
@Repository
public interface StudentUserRepository extends BaseUserRepository<StudentUser, Long> {
    Page<StudentUser> findAllByClassroomUid(UUID classroomUid, Pageable pageable);

    @Query("SELECT COUNT(s) + 1 FROM StudentUser s " + 
        "WHERE s.classroom.internalId = :classroomInternalId AND s.xp > :userXp")
    Optional<Long> findRankByClassroomInternalIdAndXp(Long classroomInternalId, Long userXp);
}
