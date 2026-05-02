package no.safety.backend.weeklymystery.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record WeeklyMysteryExerciseLinkRequest(
    @NotNull(message = "exerciseUid is required.")
    UUID exerciseUid
) {
}
