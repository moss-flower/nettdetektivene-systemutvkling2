package no.safety.backend.completedexercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "completed_exercises")
@IdClass(CompletedExerciseId.class)
@Getter
@Setter
@NoArgsConstructor
public class CompletedExercise {

  @Id
  @Column(name = "student_uid", nullable = false, columnDefinition = "BINARY(16)")
  private UUID studentUid;

  @Id
  @Column(name = "exercise_uid", nullable = false, columnDefinition = "BINARY(16)")
  private UUID exerciseUid;
}
