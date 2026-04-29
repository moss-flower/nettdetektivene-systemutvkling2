package no.safety.backend.weeklymystery.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object for requesting persistence of Weekly Mystery prompt objects.
 */
public record WeeklyMysteryPromptCreateRequest(
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "title size cannot exceed 100 characters")
    String title,

    @NotBlank(message = "prompt text is required")
    String prompt,

    @NotBlank(message = "clue required")
    String clue,

    @NotNull(message = "please include scheduled date for release")
    LocalDate date,

    Set<UUID> publicClassroomIds
) {
    @JsonCreator
    public WeeklyMysteryPromptCreateRequest {}
}
