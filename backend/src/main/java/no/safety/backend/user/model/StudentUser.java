package no.safety.backend.user.model;

<<<<<<< HEAD
import jakarta.persistence.Embedded;
=======
import java.util.concurrent.ThreadLocalRandom;

>>>>>>> 4a334e9 (fix(leaderboard-api): Fixed bugs.)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
<<<<<<< HEAD
import jakarta.persistence.PreUpdate;
=======
>>>>>>> 4a334e9 (fix(leaderboard-api): Fixed bugs.)
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import no.safety.backend.avatar.model.AvatarConfig;
import no.safety.backend.classroom.model.Classroom;

/**
 * Defines a student user entity which extends {@link User}
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class StudentUser extends User {

<<<<<<< HEAD
    /* The score of the user. */
    @Column(nullable = false)
    @Builder.Default
    private int score = 0;
=======
    /**
     * Initialize the optional {@link StudentUser} values.
     */
    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.xp == null) {
            this.xp = 0L;
        }
    }

  /* The XP points of the user */
  @Column(nullable = true, unique = false)
  private Long xp;
>>>>>>> 4a334e9 (fix(leaderboard-api): Fixed bugs.)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_uid", referencedColumnName = "uid")
    private Classroom classroom;

    @Embedded
    @Builder.Default
    private AvatarConfig avatarConfig = AvatarConfig.defaultConfig();

    @PrePersist
    @PreUpdate
    private void applyAvatarDefaultIfMissing() {
        if (this.avatarConfig == null) {
            this.avatarConfig = AvatarConfig.defaultConfig();
        }
    }

    public void addScore(int score) {
        this.score += score;
    }
}
