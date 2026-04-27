package no.safety.backend.leaderboard.dto;

import no.safety.backend.user.model.StudentUser;

public record LeaderboardResponse(
    String username,
    Long xp,
    Long rank
) {
    public static LeaderboardResponse from(StudentUser studentUser, Long rank) {
        return new LeaderboardResponse(
            studentUser.getUsername(),
            studentUser.getXp(),
            rank
        );
    }
}