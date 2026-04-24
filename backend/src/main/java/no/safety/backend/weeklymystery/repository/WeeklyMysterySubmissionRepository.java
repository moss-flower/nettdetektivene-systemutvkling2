package no.safety.backend.weeklymystery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.weeklymystery.model.WeeklyMysterySubmission;

public interface WeeklyMysterySubmissionRepository extends JpaRepository<WeeklyMysterySubmission, Long>{
    
    
}
