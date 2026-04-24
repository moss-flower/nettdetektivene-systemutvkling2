package no.safety.backend.studentprofile.dto;

import java.util.UUID;

public record StudentProfileResponse(
    UUID studentUid,
    String studentName,
    int totalXp,
    int completedExercises,
    int unlockedAchievements
) {
}
