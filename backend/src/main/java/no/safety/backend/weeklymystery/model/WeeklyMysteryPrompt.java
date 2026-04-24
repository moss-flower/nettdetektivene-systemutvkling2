package no.safety.backend.weeklymystery.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.classroom.model.Classroom;

@Entity
@Table(name = "WeeklyMysteryPrompt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyMysteryPrompt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long internalId;

    @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16")
    private UUID publicUuid;

    @Column(unique = false, length = 50)
    private String weekLabel;

    @Column(unique = false, length = 255)
    private String title;

    @Column
    private String prompt;

    @Column
    private String clue;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherUser teacherUser;

    @ManyToMany
    @Builder.Default
    @JoinTable(
        name = "weekly_mystery_assigned_classrooms",
        joinColumns = @JoinColumn(name = "mystery_internal_id", referencedColumnName = "internalId"),
        inverseJoinColumns = @JoinColumn(name = "classroom_internal_id")
    )
    private Set<Classroom> classrooms = new HashSet<>();


    /**
     * Generates a random UUID upon instance creation.
     */
    @PrePersist
    protected void onCreate() {
        if(this.publicUuid == null) {
            this.publicUuid = UUID.randomUUID();
        }
    }

}

