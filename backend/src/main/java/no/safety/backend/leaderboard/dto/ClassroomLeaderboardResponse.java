package no.safety.backend.leaderboard.dto;

import java.util.UUID;

public record ClassroomLeaderboardResponse(
    UUID classroomPublicUuid,
    String className,
    Long score,
    Long rank
) {
    public ClassroomLeaderboardResponse(UUID classroomPublicUuid, String className, Number score, Number rank) {
        this(
            classroomPublicUuid,
            className,
            score == null ? 0L : score.longValue(),
            rank == null ? 0L : rank.longValue()
        );
    }

    public static ClassroomLeaderboardResponse from(UUID classroomPublicUuid, String className, Long totalScore, Long rank) {
        return new ClassroomLeaderboardResponse(
            classroomPublicUuid,
            className,
            totalScore,
            rank
        );
    }
}
