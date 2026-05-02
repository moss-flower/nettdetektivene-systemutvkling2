package no.safety.backend.achievements.dto;

import lombok.Getter;
import lombok.Setter;
import no.safety.backend.achievements.model.AchievementCategory;

/**
 * Request DTO for creating or updating an achievement definition.
 */
@Getter
@Setter
public class AchievementRequest {
    /** Display name of the achievement. */
    private String title;
    /** Detailed description of the achievement. */
    private String description;
    /** Category classifying the type of achievement. */
    private AchievementCategory category;
    /** Source identifier for the exercise that awards this achievement. */
    private String source;
    /** Optional description of the reward for unlocking this achievement. */
    private String reward;
}
