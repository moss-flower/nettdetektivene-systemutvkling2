package no.safety.backend.excercise.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.task.model.Task;

/**
 * Entity representing an exercise in the system.
 * Contains title, difficulty, description, hint, associated notepad entry, and a set of tasks.
 * Uses a public UUID for API calls and an internal ID for database operations.
 */
@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

  /** Internal database ID (auto-generated). */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long internalId;

  /** Public UUID for API calls (generated on persist if null). */
  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID publicId;

  /** The title of the exercise (max 100 chars). */
  @Column(nullable = false, length = 100)
  private String title;

  /** The difficulty level name (max 50 chars). */
  @Column(nullable = false, length = 50)
  private String difficultyName;

  /** Detailed description of the exercise (TEXT). */
  @Column(columnDefinition = "TEXT")
  private String description;

  /** Hint text for the exercise (TEXT). */
  @Column(columnDefinition = "TEXT")
  private String hint;

  /** Optional notepad entry associated with this exercise. */
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "notepad_entry_id")
  private NotepadEntry notepadEntry;

  /** Set of tasks belonging to this exercise (lazy loaded, cascade all). */
  @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Task> tasks = new HashSet<>();

  /**
   * Generates a public UUID if not already set before persisting to database.
   */
  @PrePersist
  private void OnCreate() {
    if (this.publicId == null) {
      this.publicId = UUID.randomUUID();
    }
  }

  /**
   * Compares exercises based on their publicId.
   *
   * @param o the object to compare
   * @return true if the exercises have the same publicId
   */
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

  /**
   * Returns a hash code based on the class.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
