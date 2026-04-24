package no.safety.backend.weeklymystery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WeeklyMysteryPromptCreateRequest(
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "title size cannot exceed 100 characters")
    String title,

    @NotBlank(message = "prompt text is required")
    String prompt,

    @NotBlank(message = "clue required")
    String clue
) {
    
}
