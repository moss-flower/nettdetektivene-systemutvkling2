package no.safety.backend.station.dto;

import java.util.List;
import java.util.UUID;

public record StationDataResponse(
    UUID uid,
    String title,
    String description,
    String image,
    String backgroundColor,
    List<UUID> exerciseUids,
    boolean isBoss,
    List<Boolean> completedExercises,
    int completedExerciseCount,
    int totalExerciseCount,
    boolean isCompleted,
    boolean isUnlocked,
    boolean isTeacherPreview,
    List<StationExerciseDataResponse> exercises
) {
}
