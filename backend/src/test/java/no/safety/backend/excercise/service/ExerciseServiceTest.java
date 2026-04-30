package no.safety.backend.excercise.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import no.safety.backend.excercise.dto.ExerciseRequest;
import no.safety.backend.excercise.dto.ExerciseResponse;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.task.content.MultipleChoiceContent;
import no.safety.backend.task.dto.TaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

  @Mock
  private ExerciseRepository exerciseRepository;

  @InjectMocks
  private ExerciseService exerciseService;

  @Test
  void create_mapsOptionalNotepadEntry() {
    ExerciseRequest request = requestWithNotepadEntry();
    when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ExerciseResponse response = exerciseService.create(request);

    ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
    verify(exerciseRepository).save(captor.capture());
    Exercise savedExercise = captor.getValue();

    assertThat(savedExercise.getNotepadEntry()).isNotNull();
    assertThat(savedExercise.getNotepadEntry().getTitle()).isEqualTo("Tips");
    assertThat(savedExercise.getNotepadEntry().getType()).isEqualTo(NotepadEntryType.TIP);
    assertThat(response.notepadEntry()).isNotNull();
    assertThat(response.notepadEntry().content()).isEqualTo("Husk denne regelen.");
  }

  @Test
  void update_clearsNotepadEntryWhenRequestDoesNotIncludeIt() {
    UUID exerciseId = UUID.randomUUID();
    Exercise existing = new Exercise();
    existing.setPublicId(exerciseId);
    existing.setTitle("Old");
    existing.setDifficultyName("Old");
    existing.setHint("Old");
    no.safety.backend.notepad.model.NotepadEntry existingEntry = new no.safety.backend.notepad.model.NotepadEntry();
    existingEntry.setTitle("Old");
    existingEntry.setType(NotepadEntryType.TIP);
    existingEntry.setContent("Old");
    existingEntry.setSource("Old");
    existing.setNotepadEntry(existingEntry);

    when(exerciseRepository.findByPublicId(exerciseId)).thenReturn(Optional.of(existing));

    ExerciseResponse response = exerciseService.update(exerciseId, new ExerciseRequest(
        "New title",
        "Easy",
        "Desc",
        "Hint",
        null,
        List.of(singleTask())
    ));

    assertThat(existing.getNotepadEntry()).isNull();
    assertThat(response.notepadEntry()).isNull();
  }

  private static ExerciseRequest requestWithNotepadEntry() {
    return new ExerciseRequest(
        "Exercise",
        "Easy",
        "Description",
        "Hint",
        new NotepadEntryCreateRequest("Tips", NotepadEntryType.TIP, "Husk denne regelen.", "Station"),
        List.of(singleTask())
    );
  }

  private static TaskRequest singleTask() {
    return new TaskRequest(
        "Intro",
        "Correct",
        "Incorrect",
        new MultipleChoiceContent(
            new MultipleChoiceContent.Prompt("image", "alt", "title", "text"),
            List.of(new MultipleChoiceContent.Option("a", "A")),
            "a"
        )
    );
  }
}
