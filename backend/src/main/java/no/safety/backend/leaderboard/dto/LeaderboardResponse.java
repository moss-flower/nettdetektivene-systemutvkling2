package no.safety.backend.leaderboard.dto;

import no.safety.backend.user.model.StudentUser;

public record LeaderboardResponse(
    String username,
    int score,
    Long rank
) {
    public static LeaderboardResponse from(StudentUser studentUser, Long rank) {
        return new LeaderboardResponse(
            studentUser.getUsername(),
            studentUser.getScore(),
            rank
        );
    }
}