package no.safety.backend.completedexercise.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.service.CompletedExerciseService;
import no.safety.backend.user.model.StudentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletedExerciseControllerTest {

  @Mock
  private CompletedExerciseService completedExerciseService;

  @InjectMocks
  private CompletedExerciseController completedExerciseController;

  @Test
  void create_delegatesToService() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    UserPrincipal principal = UserPrincipal.fromEntity(student);
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);
    CompletedExerciseResponse expected = new CompletedExerciseResponse(studentUid, exerciseUid);

    when(completedExerciseService.create(request, student)).thenReturn(expected);

    CompletedExerciseResponse response = completedExerciseController.create(request, principal);

    assertSame(expected, response);
    assertEquals(exerciseUid, response.exerciseUid());
    verify(completedExerciseService).create(request, student);
  }

  @Test
  void getMyCompletedExercises_delegatesToService() {
    UUID studentUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    UserPrincipal principal = UserPrincipal.fromEntity(student);
    List<CompletedExerciseResponse> expected = List.of(
        new CompletedExerciseResponse(studentUid, UUID.randomUUID())
    );

    when(completedExerciseService.getMyCompletedExercises(student)).thenReturn(expected);

    List<CompletedExerciseResponse> response = completedExerciseController.getMyCompletedExercises(
        principal);

    assertSame(expected, response);
    assertEquals(1, response.size());
    verify(completedExerciseService).getMyCompletedExercises(student);
  }
}
