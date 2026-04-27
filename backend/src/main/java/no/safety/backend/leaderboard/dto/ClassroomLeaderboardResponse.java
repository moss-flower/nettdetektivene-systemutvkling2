package no.safety.backend.leaderboard.dto;

public record ClassroomLeaderboardResponse(
    String className,
    int score,
    Long rank
) {
    public static ClassroomLeaderboardResponse from(String className, int totalScore, Long rank) {
        return new ClassroomLeaderboardResponse(
            className,
            totalScore,
            rank
        );
    }
}