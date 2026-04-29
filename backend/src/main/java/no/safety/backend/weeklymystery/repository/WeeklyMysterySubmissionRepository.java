package no.safety.backend.weeklymystery.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.weeklymystery.model.WeeklyMysterySubmission;

/**
 * Repository for retrieving weekly mystery submissions submitted by students.
 */
public interface WeeklyMysterySubmissionRepository extends JpaRepository<WeeklyMysterySubmission, Long>{
    Set<WeeklyMysterySubmission> getByWeeklyMysteryPrompt_publicUuid(UUID publicUuid);
    Optional<WeeklyMysterySubmission> findByPublicUuid(UUID publicUuid);
}
