package no.safety.backend.achievements.repository;

import java.util.Optional;
import java.util.UUID;

import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for accessing and managing achievement definitions in the
 * database.
 */
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
  Optional<Achievement> findByUUID(UUID UUID);
  Optional<Achievement> findByTitle(String title);
  List<Achievement> findAllByTitle(String title);
  Optional<Achievement> findBySource(String source);
  Optional<Achievement> findBySourceAndCategory(String source, AchievementCategory category);

}
