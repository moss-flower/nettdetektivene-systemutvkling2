package no.safety.backend.completedexercise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.ConflictException;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.user.model.StudentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompletedExerciseService {

  private final CompletedExerciseRepository completedExerciseRepository;
  private final ProgressValidationService progressValidationService;

  @Transactional
  public CompletedExerciseResponse create(CompletedExerciseRequest request, StudentUser student) {
    String exerciseId = request.exerciseId().trim();

    if (progressValidationService.isLocked(student, exerciseId)) {
      throw new ForbiddenException("Oppgaven er låst. Fullfør forrige oppgave eller stasjon først.");
    }

    if (completedExerciseRepository.existsByStudentAndExerciseId(student, exerciseId)) {
      throw new ConflictException("Exercise is already marked as completed for this student");
    }

    CompletedExercise completedExercise = new CompletedExercise();
    completedExercise.setStudent(student);
    completedExercise.setExerciseId(exerciseId);

    return CompletedExerciseResponse.from(completedExerciseRepository.save(completedExercise));
  }

  @Transactional(readOnly = true)
  public List<CompletedExerciseResponse> getMyCompletedExercises(StudentUser student) {
    return completedExerciseRepository.findAllByStudentOrderByExerciseIdAsc(student)
        .stream()
        .map(CompletedExerciseResponse::from)
        .toList();
  }
}
