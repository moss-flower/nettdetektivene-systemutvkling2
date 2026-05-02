package no.safety.backend.completedexercise.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Composite primary key class for CompletedExercise entity.
 * Implements Serializable and provides equals/hashCode based on studentUid and exerciseUid.
 */
@NoArgsConstructor
@AllArgsConstructor
public class CompletedExerciseId implements Serializable {

  /** The public UUID of the student. */
  private UUID studentUid;

  /** The public UUID of the exercise. */
  private UUID exerciseUid;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompletedExerciseId that)) {
      return false;
    }
    return Objects.equals(studentUid, that.studentUid) && Objects.equals(exerciseUid, that.exerciseUid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studentUid, exerciseUid);
  }
}
