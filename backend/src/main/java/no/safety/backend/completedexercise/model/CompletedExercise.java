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

/**
 * Entity representing a completed exercise by a student.
 * Uses a composite primary key consisting of student UID and exercise UID.
 * This is a join table entity that tracks which exercises have been completed by which students.
 */
@Entity
@Table(name = "completed_exercises")
@IdClass(CompletedExerciseId.class)
@Getter
@Setter
@NoArgsConstructor
public class CompletedExercise {

  /** The public UUID of the student who completed the exercise. */
  @Id
  @Column(name = "student_uid", nullable = false, columnDefinition = "BINARY(16)")
  private UUID studentUid;

  /** The public UUID of the exercise that was completed. */
  @Id
  @Column(name = "exercise_uid", nullable = false, columnDefinition = "BINARY(16)")
  private UUID exerciseUid;
}
