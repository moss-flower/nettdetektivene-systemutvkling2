package no.safety.backend.completedexercise.dto;

import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;

public record CompletedExerciseResponse(
    UUID studentId,
    UUID exerciseUid
) {

  public static CompletedExerciseResponse from(CompletedExercise completedExercise) {
    return new CompletedExerciseResponse(
        completedExercise.getStudent().getPublicUuid(),
        completedExercise.getExerciseUid()
    );
  }
}
