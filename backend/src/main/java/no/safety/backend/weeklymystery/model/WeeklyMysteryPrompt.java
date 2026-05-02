package no.safety.backend.weeklymystery.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.user.model.TeacherUser;


/**
 * Entity that represents a weekly mystery object.
 * Has public and private ID's, and fields for plugging in info.
 */
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

    @Column(updatable = false, nullable = false, unique = true)
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
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherUser teacherUser;


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

