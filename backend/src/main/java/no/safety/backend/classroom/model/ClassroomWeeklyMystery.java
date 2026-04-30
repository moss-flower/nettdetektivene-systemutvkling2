package no.safety.backend.classroom.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;

/**
 * Represents a Many-to-Many database connection between a Classroom
 * and a previous Weekly Mystery.
 */
@Entity
@Getter
@Setter
@Table(name = "classroom_weekly_mystery_history")
public class ClassroomWeeklyMystery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_internal_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_mystery_internal_id")
    private WeeklyMysteryPrompt weeklyMysteryPrompt;

    private LocalDate assignedDate;
}
