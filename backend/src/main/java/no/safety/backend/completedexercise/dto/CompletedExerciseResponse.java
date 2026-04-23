package no.safety.backend.completedexercise.dto;

import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;

public record CompletedExerciseResponse(
    UUID studentUid,
    UUID exerciseUid
) {

  public static CompletedExerciseResponse from(CompletedExercise completedExercise) {
    return new CompletedExerciseResponse(
        completedExercise.getStudentUid(),
        completedExercise.getExerciseUid()
    );
  }
}
