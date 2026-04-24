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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.StudentUser;

@Entity
@Table(name = "mystery_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMysterySubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(name = "public_Uuid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID publicUuid;

    @Column
    private String message;

    @Column
    private String fileName;

    @Column
    private String storageKey;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentUser submitterStudentUser;

    @JoinColumn(name = "weekly_mystery_prompt_id")
    private WeeklyMysteryPrompt weeklyMysteryPrompt;

    @PrePersist
    private void onCreate() {
        if (publicUuid == null) {
            publicUuid = UUID.randomUUID();
        }
    }
}
