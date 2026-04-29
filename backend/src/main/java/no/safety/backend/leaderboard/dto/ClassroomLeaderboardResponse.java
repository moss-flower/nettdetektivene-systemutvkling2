package no.safety.backend.leaderboard.dto;

import java.util.UUID;

public final class ClassroomLeaderboardResponse {
    private final UUID classroomPublicUuid;
    private final String className;
    private final Long score;
    private final Long rank;

    public ClassroomLeaderboardResponse(UUID classroomPublicUuid, String className, Long score, Long rank) {
        this.classroomPublicUuid = classroomPublicUuid;
        this.className = className;
        this.score = score;
        this.rank = rank;
    }

    public ClassroomLeaderboardResponse(UUID classroomPublicUuid, String className, Number score, Number rank) {
        this(
            classroomPublicUuid,
            className,
            score == null ? 0L : score.longValue(),
            rank == null ? 0L : rank.longValue()
        );
    }

    public UUID classroomPublicUuid() {
        return classroomPublicUuid;
    }

    public String className() {
        return className;
    }

    public Long score() {
        return score;
    }

    public Long rank() {
        return rank;
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
