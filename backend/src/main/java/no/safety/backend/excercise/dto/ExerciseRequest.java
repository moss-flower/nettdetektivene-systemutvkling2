package no.safety.backend.excercise.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.task.dto.TaskRequest;

/**
 * DTO for creating or updating an exercise.
 *
 * @param title the exercise title (required, max 100 chars)
 * @param difficultyName the difficulty level name (required, max 50 chars)
 * @param description the exercise description (max 2000 chars)
 * @param hint the hint text for the exercise (required, max 2000 chars)
 * @param notepadEntry optional notepad entry to create with the exercise
 * @param tasks the list of tasks for this exercise (must have at least one)
 */
public record ExerciseRequest(
    @NotBlank(message = "title is required.")
    @Size(max = 100, message = "title cannot exceed 100 characters.")
    String title,

    @NotBlank(message = "difficultyName is required.")
    @Size(max = 50, message = "difficultyName cannot exceed 50 characters.")
    String difficultyName,

    @Size(max = 2000, message = "description cannot exceed 2000 characters.")
    String description,

    @NotBlank(message = "hint is required.")
    @Size(max = 2000, message = "hint cannot exceed 2000 characters.")
    String hint,

    @Valid
    NotepadEntryCreateRequest notepadEntry,

    @NotEmpty(message = "An exercise must contain at least one task.")
    @Valid
    List<TaskRequest> tasks
) {
}
