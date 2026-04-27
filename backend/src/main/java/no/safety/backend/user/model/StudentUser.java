package no.safety.backend.user.model;

import java.util.concurrent.ThreadLocalRandom;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "classroom_uid", referencedColumnName = "uid")
  private Classroom classroom;

}
