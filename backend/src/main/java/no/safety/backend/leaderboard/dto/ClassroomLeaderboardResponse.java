package no.safety.backend.leaderboard.dto;

import java.util.UUID;

/**
 * DTO for returning classroom leaderboard data.
 *
 * @param classroomPublicUuid the public UUID of the classroom
 * @param className the name of the classroom
 * @param score the total score of all students in the classroom
 * @param rank the rank of the classroom based on total score
 */
public record ClassroomLeaderboardResponse(
    UUID classroomPublicUuid,
    String className,
    Long score,
    Long rank
) {
    /**
     * Compact constructor that handles null Number values by converting them to 0L.
     *
     * @param classroomPublicUuid the classroom public UUID
     * @param className the classroom name
     * @param score the total score (can be null)
     * @param rank the rank (can be null)
     */
    public ClassroomLeaderboardResponse(UUID classroomPublicUuid, String className, Number score, Number rank) {
        this(
            classroomPublicUuid,
            className,
            score == null ? 0L : score.longValue(),
            rank == null ? 0L : rank.longValue()
        );
    }

    /**
     * Creates a ClassroomLeaderboardResponse from individual values.
     *
     * @param classroomPublicUuid the classroom public UUID
     * @param className the classroom name
     * @param totalScore the total score of the classroom
     * @param rank the rank of the classroom
     * @return a new ClassroomLeaderboardResponse
     */
    public static ClassroomLeaderboardResponse from(UUID classroomPublicUuid, String className, Long totalScore, Long rank) {
        return new ClassroomLeaderboardResponse(
            classroomPublicUuid,
            className,
            totalScore,
            rank
        );
    }
}
