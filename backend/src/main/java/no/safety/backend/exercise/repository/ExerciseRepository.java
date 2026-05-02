package no.safety.backend.excercise.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.excercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing Exercise entities.
 * Provides methods for finding exercises by their public UUID.
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  /**
   * Finds an exercise by its public UUID.
   *
   * @param publicId the public UUID of the exercise
   * @return an Optional containing the Exercise if found
   */
  Optional<Exercise> findByPublicId(UUID publicId);
}
