package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;

public record WeeklyMysterPromptResponse(
    UUID publicId,
    String weekLabel,
    String title,
    String prompt,
    String clue
) {
    public static WeeklyMysterPromptResponse fromEntity(WeeklyMysteryPrompt prompt){
        return new WeeklyMysterPromptResponse(
            prompt.getPublicUuid(),
            prompt.getWeekLabel(),
            prompt.getTitle(),
            prompt.getPrompt(),
            prompt.getClue());
    }
}
