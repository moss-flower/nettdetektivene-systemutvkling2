package no.safety.backend.leaderboard.dto;

import java.util.UUID;

public record ClassroomLeaderboardResponse(
        UUID classroomPublicUuid,
        String className,
        Long score,
        Long rank) {
    public static ClassroomLeaderboardResponse from(UUID classroomPublicUuid, String className, Long totalScore, Long rank) {
        return new ClassroomLeaderboardResponse(
                classroomPublicUuid,
                className,
                totalScore,
                rank);
    }
}