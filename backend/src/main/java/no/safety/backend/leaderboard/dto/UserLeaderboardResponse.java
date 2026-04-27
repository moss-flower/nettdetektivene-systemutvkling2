package no.safety.backend.leaderboard.dto;

import no.safety.backend.user.model.StudentUser;

public record UserLeaderboardResponse(
    String username,
    int score,
    Long rank
) {
    public static UserLeaderboardResponse from(StudentUser studentUser, Long rank) {
        return new UserLeaderboardResponse(
            studentUser.getUsername(),
            studentUser.getScore(),
            rank
        );
    }
}