package no.safety.backend.leaderboard.dto;

import java.util.UUID;

public interface ClassroomLeaderboardProjection {
    UUID getClassroomPublicUuid();
    String getClassName();
    Number getScore();
    Number getRank();
}
