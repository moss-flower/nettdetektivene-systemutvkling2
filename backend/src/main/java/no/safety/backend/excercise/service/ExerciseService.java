package no.safety.backend.excercise.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.excercise.dto.ExerciseRequest;
import no.safety.backend.excercise.dto.ExerciseResponse;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.task.dto.TaskRequest;
import no.safety.backend.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseService {

  private final ExerciseRepository exerciseRepo;

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

  @Transactional(readOnly = true)
  public ExerciseResponse get(UUID publicId) {
    return ExerciseResponse.from(require(publicId));
  }

  @Transactional(readOnly = true)
  public Page<ExerciseResponse> list(@PageableDefault Pageable pageable) {
    return exerciseRepo.findAll(pageable).map(ExerciseResponse::from);
  }

  @Transactional
  public ExerciseResponse update(UUID publicId, ExerciseRequest req) {
    Exercise e = require(publicId);
    applyScalarFields(e, req);

    e.getTasks().clear();
    List<Task> updatedTasks = req.tasks().stream()
        .map(tr -> newTask(e, tr))
        .toList();
    e.getTasks().addAll(updatedTasks);

    return ExerciseResponse.from(e);
  }

  @Transactional
  public void delete(UUID publicId) {
    exerciseRepo.delete(require(publicId));
  }

  private void applyScalarFields(Exercise e, ExerciseRequest req) {
    e.setTitle(req.title());
    e.setDifficultyName(req.difficultyName());
    e.setDescription(req.description());
  }

  private Task newTask(Exercise e, TaskRequest tr) {
    Task t = new Task();
    t.setExercise(e);
    copyTaskFields(t, tr);
    return t;
  }

  private void copyTaskFields(Task t, TaskRequest tr) {
    t.setIntroText(tr.introText());
    t.setFeedbackCorrect(tr.feedbackCorrect());
    t.setFeedbackIncorrect(tr.feedbackIncorrect());
    t.setContent(tr.content());
    t.setTemplateKind(null);
  }

  private Exercise require(UUID publicId) {
    return exerciseRepo.findByPublicId(publicId)
        .orElseThrow(() -> new NotFoundException("Exercise not found"));
  }
}
