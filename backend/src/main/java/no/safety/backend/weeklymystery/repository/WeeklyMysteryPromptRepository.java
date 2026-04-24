package no.safety.backend.weeklymystery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;

public interface WeeklyMysteryPromptRepository extends JpaRepository<WeeklyMysteryPrompt, Long> {
    public WeeklyMysteryPrompt findByPublicUuid(UUID publicUuid);
}
