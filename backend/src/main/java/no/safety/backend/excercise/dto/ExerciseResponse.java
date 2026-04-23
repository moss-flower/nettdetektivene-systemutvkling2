package no.safety.backend.excercise.dto;

import java.util.List;
import java.util.UUID;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.task.dto.TaskResponse;

/**
 * Defines a {@link Exercise} response dto for delivering exercise information to client.
 * @param publicId
 * @param title
 * @param difficultyName
 * @param description
 * @param tasks
 */
public record ExerciseResponse(
    UUID publicId,
    String title,
    String difficultyName,
    String description,
    List<TaskResponse> tasks
) {

  /**
   * Static factory method to map an Exercise to this Response DTO.
   * @param e Exercise to map
   * @return ExerciseResponse mapped from the exercise
   */
  public static ExerciseResponse from(Exercise e) {
    List<TaskResponse> tasks = e.getTasks().stream()
        .map(TaskResponse::from)
        .toList();

    return new ExerciseResponse(
        e.getPublicId(),
        e.getTitle(),
        e.getDifficultyName(),
        e.getDescription(),
        tasks
    );
  }
}
