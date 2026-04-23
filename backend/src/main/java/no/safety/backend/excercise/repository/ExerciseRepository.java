package no.safety.backend.excercise.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.excercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Defines a JPA repository for exercises.
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  Optional<Exercise> findByPublicId(UUID publicId);
}
