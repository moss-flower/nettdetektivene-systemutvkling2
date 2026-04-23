package no.safety.backend.completedexercise.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CompletedExerciseId implements Serializable {

  private UUID studentUid;
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
