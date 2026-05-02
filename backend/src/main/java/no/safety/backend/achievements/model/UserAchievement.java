package no.safety.backend.achievements.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.StudentUser;

import java.time.Instant;

/**
 * Entity linking a student to an unlocked achievement. Each record
 * represents a student who has earned a specific achievement, along with
 * the timestamp when it was unlocked.
 */
@Entity
@Table(name = "user_achievements", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "achievement_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class UserAchievement {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The student who unlocked the achievement. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /** The achievement that was unlocked. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    /**
     * Timestamp when the achievement was unlocked, set automatically on creation.
     */
    @Column(nullable = false, updatable = false)
    private Instant unlockedAt;

    /**
     * Sets the unlock timestamp if not already set before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        if (this.unlockedAt == null) {
            this.unlockedAt = Instant.now();
        }
    }
}
