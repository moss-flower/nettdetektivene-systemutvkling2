package no.safety.backend.achievements.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO containing achievement data for API responses. Includes
 * whether the achievement has been unlocked by the requesting user.
 */
@Getter
@Builder
public class AchievementResponse {
    /** Public UUID of the achievement. */
    private UUID UUID;
    /** Display name of the achievement. */
    private String title;
    /** Detailed description of the achievement. */
    private String description;
    /** Category of the achievement as a lowercase string. */
    private String category;
    /** Source identifier for the system that awards this achievement. */
    private String source;
    /** Whether the achievement is unlocked for the requesting user. */
    private boolean unlocked;
    /** Optional description of the reward for unlocking this achievement. */
    private String reward;
}
