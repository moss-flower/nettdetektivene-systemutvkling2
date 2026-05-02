package no.safety.backend.completedexercise.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.model.CompletedExerciseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing CompletedExercise entities.
 * Provides methods for querying completed exercises by student and exercise UIDs.
 */
@Repository
public interface CompletedExerciseRepository
    extends JpaRepository<CompletedExercise, CompletedExerciseId> {

  /**
   * Checks if a completed exercise exists for the given student and exercise.
   *
   * @param studentUid the public UUID of the student
   * @param exerciseUid the public UUID of the exercise
   * @return true if the exercise has been completed by the student, false otherwise
   */
  boolean existsByStudentUidAndExerciseUid(UUID studentUid, UUID exerciseUid);

  /**
   * Finds a completed exercise by student and exercise UIDs.
   *
   * @param studentUid the public UUID of the student
   * @param exerciseUid the public UUID of the exercise
   * @return an Optional containing the CompletedExercise if found
   */
  Optional<CompletedExercise> findByStudentUidAndExerciseUid(UUID studentUid, UUID exerciseUid);

  /**
   * Finds all completed exercises for a student, ordered by exercise UID ascending.
   *
   * @param studentUid the public UUID of the student
   * @return list of completed exercises for the student
   */
  List<CompletedExercise> findAllByStudentUidOrderByExerciseUidAsc(UUID studentUid);

  /**
   * Counts how many exercises from a given collection have been completed by a student.
   *
   * @param studentUid the public UUID of the student
   * @param exerciseUids collection of exercise UIDs to check
   * @return the count of completed exercises from the provided collection
   */
  long countByStudentUidAndExerciseUidIn(UUID studentUid, Collection<UUID> exerciseUids);

  /**
   * Counts all completed exercises for a student.
   *
   * @param studentUid the public UUID of the student
   * @return the total number of completed exercises by the student
   */
  long countByStudentUid(UUID studentUid);
}
