package no.safety.backend.completedexercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.StudentUser;

@Entity
@Table(name = "completed_exercises")
@IdClass(CompletedExerciseId.class)
@Getter
@Setter
@NoArgsConstructor
public class CompletedExercise {

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "studentid", nullable = false)
  private StudentUser student;

  @Id
  @Column(name = "exerciseid", nullable = false, length = 100)
  private String exerciseId;
}
