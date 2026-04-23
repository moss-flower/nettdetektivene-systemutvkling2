package no.safety.backend.completedexercise.dto;

import java.util.UUID;

public record CompletedExerciseRequest(
    UUID exerciseUid
) {
}
