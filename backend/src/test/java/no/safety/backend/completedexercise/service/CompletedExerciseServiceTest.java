package no.safety.backend.completedexercise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.ConflictException;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.user.model.StudentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletedExerciseServiceTest {

  @Mock
  private CompletedExerciseRepository completedExerciseRepository;

  @Mock
  private ProgressValidationService progressValidationService;

  @InjectMocks
  private CompletedExerciseService completedExerciseService;

  @Test
  void create_whenExerciseLocked_throwsForbidden() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(true);

    assertThrows(ForbiddenException.class, () -> completedExerciseService.create(request, student));

    verify(progressValidationService).isLocked(student, exerciseUid);
    verifyNoInteractions(completedExerciseRepository);
  }

  @Test
  void create_whenAlreadyCompleted_throwsConflict() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(true);

    assertThrows(ConflictException.class, () -> completedExerciseService.create(request, student));

    verify(progressValidationService).isLocked(student, exerciseUid);
    verify(completedExerciseRepository).existsByStudentUidAndExerciseUid(studentUid, exerciseUid);
  }

  @Test
  void create_whenValid_savesAndReturnsResponse() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(false);
    when(completedExerciseRepository.save(any(CompletedExercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CompletedExerciseResponse response = completedExerciseService.create(request, student);

    assertEquals(studentUid, response.studentUid());
    assertEquals(exerciseUid, response.exerciseUid());
    verify(completedExerciseRepository).save(any(CompletedExercise.class));
  }

  @Test
  void getMyCompletedExercises_returnsMappedResponses() {
    UUID studentUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    UUID exercise1 = UUID.randomUUID();
    UUID exercise2 = UUID.randomUUID();

    CompletedExercise completed1 = new CompletedExercise();
    completed1.setStudentUid(studentUid);
    completed1.setExerciseUid(exercise1);

    CompletedExercise completed2 = new CompletedExercise();
    completed2.setStudentUid(studentUid);
    completed2.setExerciseUid(exercise2);

    when(completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(studentUid))
        .thenReturn(List.of(completed1, completed2));

    List<CompletedExerciseResponse> response = completedExerciseService.getMyCompletedExercises(student);

    assertEquals(2, response.size());
    assertEquals(exercise1, response.get(0).exerciseUid());
    assertEquals(exercise2, response.get(1).exerciseUid());
    verify(completedExerciseRepository).findAllByStudentUidOrderByExerciseUidAsc(studentUid);
  }
}
