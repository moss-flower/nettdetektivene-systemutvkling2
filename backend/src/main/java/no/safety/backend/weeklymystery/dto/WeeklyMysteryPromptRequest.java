package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * A simple DTO containing the UID of the requested classroom.
 * Used to request a WeeklyMysteryPrompt for a given room.
 */
public record WeeklyMysteryPromptRequest(
    @NotNull UUID classroomUid
) {
    
}
