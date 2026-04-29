package no.safety.backend.leaderboard.dto;

public record ClassroomLeaderboardResponse(
        String classroomPublicUuid,
        String className,
        Long score,
        Long rank) {
    public static ClassroomLeaderboardResponse from(String uid, String name, Long totalScore, Long rank) {
        return new ClassroomLeaderboardResponse(
                uid,
                name,
                totalScore,
                rank);
    }
}