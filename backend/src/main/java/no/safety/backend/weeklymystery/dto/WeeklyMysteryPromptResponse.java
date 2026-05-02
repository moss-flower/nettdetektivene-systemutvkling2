package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;

/**
 * DTO for retrieving Weekly Mystery Prompts from the database
 */
public record WeeklyMysteryPromptResponse(
    UUID publicId,
    String weekLabel,
    String title,
    String prompt,
    String clue,
    UUID exerciseUid
) {
    public static WeeklyMysteryPromptResponse fromEntity(WeeklyMysteryPrompt prompt){
        return new WeeklyMysteryPromptResponse(
            prompt.getPublicUuid(),
            prompt.getWeekLabel(),
            prompt.getTitle(),
            prompt.getPrompt(),
            prompt.getClue(),
            prompt.getExercise() != null ? prompt.getExercise().getPublicId() : null);
    }
}
