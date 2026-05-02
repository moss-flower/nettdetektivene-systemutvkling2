package no.safety.backend.completedexercise.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for requesting to mark an exercise as completed.
 *
 * @param exerciseUid the public UUID of the exercise to mark as completed (required)
 */
public record CompletedExerciseRequest(
    @NotNull(message = "exerciseUid is required")
    UUID exerciseUid
) {
}
