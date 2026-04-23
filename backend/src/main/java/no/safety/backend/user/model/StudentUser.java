package no.safety.backend.user.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
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
@SuperBuilder
public class StudentUser extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
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
}
