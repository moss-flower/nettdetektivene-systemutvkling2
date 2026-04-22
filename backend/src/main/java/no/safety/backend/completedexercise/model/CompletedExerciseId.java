package no.safety.backend.completedexercise.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CompletedExerciseId implements Serializable {

  private Long student;
  private String exerciseId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompletedExerciseId that)) {
      return false;
    }
    return Objects.equals(student, that.student) && Objects.equals(exerciseId, that.exerciseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(student, exerciseId);
  }
}
