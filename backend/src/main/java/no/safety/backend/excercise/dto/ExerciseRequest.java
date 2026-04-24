package no.safety.backend.excercise.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import no.safety.backend.task.dto.TaskRequest;

public record ExerciseRequest(
    @NotBlank(message = "title is required.")
    @Size(max = 100, message = "title cannot exceed 100 characters.")
    String title,

    @NotBlank(message = "difficultyName is required.")
    @Size(max = 50, message = "difficultyName cannot exceed 50 characters.")
    String difficultyName,

    @Size(max = 2000, message = "description cannot exceed 2000 characters.")
    String description,

    @NotEmpty(message = "An exercise must contain at least one task.")
    @Valid
    List<TaskRequest> tasks
) {
}
