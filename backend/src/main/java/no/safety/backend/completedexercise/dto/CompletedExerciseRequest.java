package no.safety.backend.completedexercise.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CompletedExerciseRequest(
    @NotNull(message = "exerciseUid is required")
    UUID exerciseUid
) {
}
