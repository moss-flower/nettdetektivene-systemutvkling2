package no.safety.backend.leaderboard.dto;

import java.util.UUID;

/**
 * An interface used by the {@link ClassroomLeaderRepository} to calculate classroom nakings.
 * Used by JPA to map the query SQL columns to the correct value types.
 */
public interface ClassroomLeaderboardProjection {
    UUID getClassroomPublicUuid();
    String getClassName();
    Long getScore();
    Long getRank();
}
