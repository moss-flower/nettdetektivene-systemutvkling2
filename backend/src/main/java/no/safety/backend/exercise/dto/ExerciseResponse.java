package no.safety.backend.excercise.dto;

import java.util.List;
import java.util.UUID;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.task.dto.TaskResponse;

/**
 * DTO for returning exercise data to the client.
 *
 * @param uid the public UUID of the exercise
 * @param title the exercise title
 * @param difficultyName the difficulty level name
 * @param description the exercise description
 * @param hint the hint text for the exercise
 * @param notepadEntry optional notepad entry associated with the exercise
 * @param tasks the list of tasks belonging to this exercise
 */
public record ExerciseResponse(
    UUID uid,
    String title,
    String difficultyName,
    String description,
    String hint,
    NotepadEntryResponse notepadEntry,
    List<TaskResponse> tasks
) {

  /**
   * Creates an ExerciseResponse from an Exercise entity.
   *
   * @param e the Exercise entity to convert
   * @return a new ExerciseResponse with values from the entity
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
        e.getHint(),
        e.getNotepadEntry() == null ? null : NotepadEntryResponse.fromEntity(e.getNotepadEntry()),
        tasks
    );
  }
}
