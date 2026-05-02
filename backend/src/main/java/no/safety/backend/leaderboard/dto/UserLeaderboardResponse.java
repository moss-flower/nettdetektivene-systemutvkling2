package no.safety.backend.leaderboard.dto;

import no.safety.backend.user.model.StudentUser;

/**
 * DTO for returning user leaderboard data.
 *
 * @param username the username of the student
 * @param score the current score of the student
 * @param rank the rank of the student within their classroom
 */
public record UserLeaderboardResponse(
        String username,
        int score,
        Long rank) {
    /**
     * Creates a UserLeaderboardResponse from a StudentUser entity and rank.
     *
     * @param studentUser the student user entity
     * @param rank the rank of the student
     * @return a new UserLeaderboardResponse
     */
    public static UserLeaderboardResponse from(StudentUser studentUser, Long rank) {
        return new UserLeaderboardResponse(
                studentUser.getUsername(),
                studentUser.getScore(),
                rank);
    }
}