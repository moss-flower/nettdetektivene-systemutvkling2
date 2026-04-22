package no.safety.backend.completedexercise.dto;

import jakarta.validation.constraints.NotBlank;

public record CompletedExerciseRequest(
    @NotBlank(message = "exerciseId is required")
    String exerciseId
) {
}
