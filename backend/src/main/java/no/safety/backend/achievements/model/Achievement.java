package no.safety.backend.achievements.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an achievement definition that can be awarded to students.
 * Achievements have a title, description, category, and optional source and
 * reward fields.
 */
@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
public class Achievement {

    /** Auto-generated primary key for internal database use. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /** Public-facing UUID used for API references. */
    @Column(nullable = false, unique = true)
    private UUID UUID;

    /** Display name of the achievement. */
    @Column(nullable = false)
    private String title;

    /** Detailed description of the achievement, up to 1000 characters. */
    @Column(nullable = false, length = 1000)
    private String description;

    /** Category classifying the type of achievement. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    /** Identifier for the system or feature that awards this achievement. */
    @Column(length = 1000)
    private String source;

    /** Optional description of the reward for unlocking this achievement. */
    @Column
    private String reward;
}
