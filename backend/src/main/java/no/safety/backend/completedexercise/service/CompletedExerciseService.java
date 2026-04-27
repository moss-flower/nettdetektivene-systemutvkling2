package no.safety.backend.completedexercise.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompletedExerciseService {

  private final CompletedExerciseRepository completedExerciseRepository;
  private final ProgressValidationService progressValidationService;
  private final StudentUserRepository studentUserRepository;

  @Transactional
  public CompletedExerciseResponse create(CompletedExerciseRequest request, StudentUser student) {
    UUID studentUid = student.getPublicUuid();
    UUID exerciseUid = request.exerciseUid();

    if (progressValidationService.isLocked(student, exerciseUid)) {
      throw new ForbiddenException("Oppgaven er låst. Fullfør forrige oppgave eller stasjon først.");
    }

    boolean hasCompletedExerciseFromBefore = completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid);
    student.addScore(hasCompletedExerciseFromBefore ? 10 : 100);
    studentUserRepository.save(student);

    if (hasCompletedExerciseFromBefore) {
      return CompletedExerciseResponse.from(completedExerciseRepository.findByStudentUidAndExerciseUid(studentUid, exerciseUid)
          .orElseThrow());
    }

    CompletedExercise completedExercise = new CompletedExercise();
    completedExercise.setStudentUid(studentUid);
    completedExercise.setExerciseUid(exerciseUid);

    return CompletedExerciseResponse.from(completedExerciseRepository.save(completedExercise));
  }

  @Transactional(readOnly = true)
  public List<CompletedExerciseResponse> getMyCompletedExercises(StudentUser student) {
    return completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(student.getPublicUuid())
        .stream()
        .map(CompletedExerciseResponse::from)
        .toList();
  }
}
