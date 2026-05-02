package no.safety.backend.station.dto;

import java.util.UUID;

public record StationExerciseDataResponse(
    UUID uid,
    String title,
    String difficultyText,
    String description,
    boolean isCompleted,
    boolean isUnlocked
) {
}
