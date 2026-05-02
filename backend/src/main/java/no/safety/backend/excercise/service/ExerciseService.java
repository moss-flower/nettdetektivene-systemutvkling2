package no.safety.backend.excercise.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.excercise.dto.ExerciseReadRequest;
import no.safety.backend.excercise.dto.ExerciseRequest;
import no.safety.backend.excercise.dto.ExerciseResponse;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.task.dto.TaskRequest;
import no.safety.backend.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing exercises.
 * Handles business logic for creating, reading, updating, and deleting exercises and their tasks.
 */
@Service
@RequiredArgsConstructor
public class ExerciseService {

  private final ExerciseRepository exerciseRepo;

  /**
   * Creates a new exercise with the provided tasks.
   *
   * @param req the exercise request containing title, difficulty, tasks, etc.
   * @return the created exercise response
   */
  @Transactional
  public ExerciseResponse create(ExerciseRequest req) {
    Exercise e = new Exercise();
    applyScalarFields(e, req);
    List<Task> fresh = req.tasks().stream()
        .map(tr -> newTask(e, tr))
        .toList();
    e.getTasks().addAll(fresh);
    return ExerciseResponse.from(exerciseRepo.save(e));
  }

  /**
   * Retrieves a single exercise by its public UUID.
   *
   * @param uid the public UUID of the exercise
   * @return the exercise response
   * @throws NotFoundException if the exercise is not found
   */
  @Transactional(readOnly = true)
  public ExerciseResponse get(UUID uid) {
    return ExerciseResponse.from(require(uid));
  }

  /**
   * Retrieves a paginated list of all exercises.
   *
   * @param req the read request containing pagination parameters
   * @return a page of exercise responses
   */
  @Transactional(readOnly = true)
  public Page<ExerciseResponse> list(ExerciseReadRequest req) {
    return exerciseRepo.findAll(req.toPageable()).map(ExerciseResponse::from);
  }

  /**
   * Updates an existing exercise by replacing all fields and tasks.
   * Existing tasks are cleared and replaced with the new set.
   *
   * @param uid the public UUID of the exercise to update
   * @param req the exercise request containing updated fields and tasks
   * @return the updated exercise response
   * @throws NotFoundException if the exercise is not found
   */
  @Transactional
  public ExerciseResponse update(UUID uid, ExerciseRequest req) {
    Exercise e = require(uid);
    applyScalarFields(e, req);

    e.getTasks().clear();
    List<Task> updatedTasks = req.tasks().stream()
        .map(tr -> newTask(e, tr))
        .toList();
    e.getTasks().addAll(updatedTasks);

    return ExerciseResponse.from(exerciseRepo.save(e));
  }

  /**
   * Deletes an exercise and all its associated tasks.
   *
   * @param uid the public UUID of the exercise to delete
   * @throws NotFoundException if the exercise is not found
   */
  @Transactional
  public void delete(UUID uid) {
    exerciseRepo.delete(require(uid));
  }

  /**
   * Applies scalar field values from the request to the exercise entity.
   *
   * @param e the exercise entity to update
   * @param req the exercise request containing the new values
   */
  private void applyScalarFields(Exercise e, ExerciseRequest req) {
    e.setTitle(req.title());
    e.setDifficultyName(req.difficultyName());
    e.setDescription(req.description());
    e.setHint(req.hint());
    applyNotepadEntryFields(e, req.notepadEntry());
  }

  /**
   * Applies notepad entry fields from the request to the exercise's notepad entry.
   * Creates a new notepad entry if one doesn't exist.
   *
   * @param exercise the exercise entity to update
   * @param request the notepad entry create request (can be null)
   */
  private void applyNotepadEntryFields(Exercise exercise, NotepadEntryCreateRequest request) {
    if (request == null) {
      exercise.setNotepadEntry(null);
      return;
    }

    NotepadEntry notepadEntry = exercise.getNotepadEntry();
    if (notepadEntry == null) {
      notepadEntry = new NotepadEntry();
      exercise.setNotepadEntry(notepadEntry);
    }

    notepadEntry.setTitle(request.title());
    notepadEntry.setType(request.type());
    notepadEntry.setContent(request.content());
    notepadEntry.setSource(request.source());
  }

  /**
   * Creates a new Task entity from a TaskRequest and associates it with an exercise.
   *
   * @param e the exercise to associate the task with
   * @param tr the task request containing task data
   * @return a new Task entity
   */
  private Task newTask(Exercise e, TaskRequest tr) {
    Task t = new Task();
    t.setExercise(e);
    copyTaskFields(t, tr);
    return t;
  }

  /**
   * Copies fields from a TaskRequest to a Task entity.
   *
   * @param t the task entity to update
   * @param tr the task request containing the new values
   */
  private void copyTaskFields(Task t, TaskRequest tr) {
    t.setIntroText(tr.introText());
    t.setFeedbackCorrect(tr.feedbackCorrect());
    t.setFeedbackIncorrect(tr.feedbackIncorrect());
    t.setContent(tr.content());
    t.setTemplateKind(null);
  }

  /**
   * Finds an exercise by its public UUID or throws NotFoundException.
   *
   * @param uid the public UUID of the exercise
   * @return the Exercise entity
   * @throws NotFoundException if the exercise is not found
   */
  private Exercise require(UUID uid) {
    return exerciseRepo.findByPublicId(uid)
        .orElseThrow(() -> new NotFoundException("Exercise not found"));
  }
}
