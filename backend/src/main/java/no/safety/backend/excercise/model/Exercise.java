package no.safety.backend.excercise.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.task.model.Task;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

  //Internal database ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //Public UID for api calls
  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID publicId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 50)
  private String difficultyName;

  @Column(length = 100)
  private String description;

  @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Task> tasks = new HashSet<>();

  @PrePersist
  private void OnCreate() {
    if (this.publicId == null) {
      this.publicId = UUID.randomUUID();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Exercise exercise)) {
      return false;
    }
    return publicId != null && publicId.equals(exercise.getPublicId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
