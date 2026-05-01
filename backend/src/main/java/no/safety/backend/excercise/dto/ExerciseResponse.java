package no.safety.backend.excercise.dto;

import java.util.List;
import java.util.UUID;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.dto.NotepadEntryResponse;
import no.safety.backend.task.dto.TaskResponse;

public record ExerciseResponse(
    UUID uid,
    String title,
    String difficultyName,
    String description,
    String hint,
    NotepadEntryResponse notepadEntry,
    List<TaskResponse> tasks
) {

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
