package no.safety.backend.leaderboard.dto;

import java.util.UUID;

/**
 * Projection interface for classroom leaderboard data.
 * Used for JPQL queries that return aggregated classroom score and rank data.
 */
public interface ClassroomLeaderboardProjection {
    /**
     * Returns the public UUID of the classroom.
     *
     * @return the classroom's public UUID
     */
    UUID getClassroomPublicUuid();

    /**
     * Returns the name of the classroom.
     *
     * @return the classroom name
     */
    String getClassName();

    /**
     * Returns the total score of all students in the classroom.
     *
     * @return the total score as a Number
     */
    Number getScore();

    /**
     * Returns the rank of the classroom based on total score.
     *
     * @return the rank as a Number
     */
    Number getRank();
}
