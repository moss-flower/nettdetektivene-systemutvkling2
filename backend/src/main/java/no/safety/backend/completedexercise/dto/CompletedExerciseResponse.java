package no.safety.backend.completedexercise.dto;

import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;

/**
 * Data Transfer Object (DTO) for returning completed exercise information.
 *
 * @param studentUid the public UUID of the student who completed the exercise
 * @param exerciseUid the public UUID of the completed exercise
 */
public record CompletedExerciseResponse(
    UUID studentUid,
    UUID exerciseUid
) {

  /**
   * Creates a CompletedExerciseResponse from a CompletedExercise entity.
   *
   * @param completedExercise the completed exercise entity to convert
   * @return a new CompletedExerciseResponse with values from the entity
   */
  public static CompletedExerciseResponse from(CompletedExercise completedExercise) {
    return new CompletedExerciseResponse(
        completedExercise.getStudentUid(),
        completedExercise.getExerciseUid()
    );
  }
}
