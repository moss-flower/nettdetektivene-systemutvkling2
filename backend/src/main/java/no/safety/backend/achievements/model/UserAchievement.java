package no.safety.backend.achievements.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.StudentUser;

import java.time.Instant;

@Entity
@Table(
    name = "user_achievements",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "achievement_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class UserAchievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  private StudentUser student;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "achievement_id", nullable = false)
  private Achievement achievement;

  @Column(nullable = false, updatable = false)
  private Instant unlockedAt;

  @PrePersist
  protected void onCreate() {
    if (this.unlockedAt == null) {
      this.unlockedAt = Instant.now();
    }
  }
}