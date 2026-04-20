package no.safety.backend.achievements.repository;

import java.util.Optional;
import no.safety.backend.achievements.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

  Optional<Achievement> findByCode(String code);
}