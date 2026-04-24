package no.safety.backend.studentprofile.dto;

import java.util.UUID;

public record StudentProfileResponse(
    UUID studentUid,
    String studentName,
    int currentLevel,
    int currentXp,
    int nextLevelXp,
    int completedExercises,
    int unlockedAchievements
) {
}
