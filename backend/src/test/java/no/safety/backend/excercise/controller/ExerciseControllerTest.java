package no.safety.backend.excercise.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import no.safety.backend.auth.service.JwtService;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.excercise.dto.ExerciseReadRequest;
import no.safety.backend.excercise.dto.ExerciseRequest;
import no.safety.backend.excercise.dto.ExerciseResponse;
import no.safety.backend.excercise.service.ExerciseService;
import no.safety.backend.task.content.MultipleChoiceContent;
import no.safety.backend.task.content.MultipleChoiceContent.Option;
import no.safety.backend.task.content.MultipleChoiceContent.Prompt;
import no.safety.backend.task.content.TaskContent;
import no.safety.backend.task.dto.TaskRequest;
import no.safety.backend.task.dto.TaskResponse;
import no.safety.backend.task.model.TemplateKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import no.safety.backend.auth.service.JwtService;

@WebMvcTest(ExerciseController.class)
@AutoConfigureRestTestClient
@EnableMethodSecurity
class ExerciseControllerTest {

  @Autowired
  RestTestClient client;

  @MockitoBean
  private ExerciseService mockService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  private ExerciseRequest validRequest;
  private ExerciseResponse savedResponse;
  private UUID publicId;

  @BeforeEach
  void setUp() {
    TaskContent tc1 = new MultipleChoiceContent(
        new Prompt("https://placehold.co/1200x720?text=Troverdig+nyhet", "bilde",
            "Velg riktig side", "Du må velge den du mener er best."),
        List.of(new Option("A", "A"), new Option("B", "B"), new Option("C", "C")), "C");
    TaskRequest tr1 = new TaskRequest(
        "Her er det om å ha best smak mellom de tre første bokstavene i alfabetet",
        "Alle vet at C er best! Bra jobba!", "Tenk deg godt om.", tc1);

    validRequest = new ExerciseRequest("Bokstavenes rangering", "67% Vanskelig",
        "Hvordan rangerer du bokstavene i alfabetet?", "Se etter hvilket alternativ som passer best.", List.of(tr1));

    publicId = UUID.randomUUID();
    TaskResponse taskResp = new TaskResponse(
        1L,
        TemplateKind.MULTIPLE_CHOICE,
        tr1.introText(),
        tr1.feedbackCorrect(),
        tr1.feedbackIncorrect(),
        tc1);
    savedResponse = new ExerciseResponse(
        publicId,
        validRequest.title(),
        validRequest.difficultyName(),
        validRequest.description(),
        validRequest.hint(),
        List.of(taskResp));
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void create_withValidRequest_successfulAnd201Created() {
    when(mockService.create(any(ExerciseRequest.class))).thenReturn(savedResponse);

    client.post().uri("/api/v1/exercises")
        .contentType(MediaType.APPLICATION_JSON)
        .body(validRequest)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(ExerciseResponse.class).value(response -> {
          assertThat(response.title()).isEqualTo("Bokstavenes rangering");
          assertThat(response.uid()).isEqualTo(publicId);
          assertThat(response.tasks()).hasSize(1);
        });
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void create_withInvalidRequest_returns400BadRequest() {
    ExerciseRequest invalidRequest = new ExerciseRequest("", "", "", "", null);
    client.post().uri("/api/v1/exercises")
        .contentType(MediaType.APPLICATION_JSON)
        .body(invalidRequest)
        .exchange()
        .expectStatus().isBadRequest();

    verifyNoInteractions(mockService);
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void create_withStudentRole_returnsForbiddenInWebMvcSlice() {
    client.post().uri("/api/v1/exercises")
        .contentType(MediaType.APPLICATION_JSON)
        .body(validRequest)
        .exchange()
        .expectStatus().isForbidden();

    verifyNoInteractions(mockService);
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void get_withValidPublicId_returnsExerciseResponse() {
    when(mockService.get(publicId)).thenReturn(savedResponse);

    client.get().uri("/api/v1/exercises/{publicId}", publicId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExerciseResponse.class).value(response -> {
          assertThat(response.uid()).isEqualTo(publicId);
          assertThat(response.title()).isEqualTo("Bokstavenes rangering");
          assertThat(response.tasks()).hasSize(1);
        });
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void get_withUnknownPublicId_returns404NotFound() {
    when(mockService.get(any(UUID.class)))
        .thenThrow(new NotFoundException("Exercise not found"));

    client.get().uri("/api/v1/exercises/{publicId}", UUID.randomUUID())
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void getAll_withDefaults_returns200WithPage() {
    Page<ExerciseResponse> page = new PageImpl<>(List.of(savedResponse));
    when(mockService.list(any(ExerciseReadRequest.class))).thenReturn(page);

    client.get().uri("/api/v1/exercises")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getAll_withStudentRole_returnsForbiddenInWebMvcSlice() {
    client.get().uri("/api/v1/exercises")
        .exchange()
        .expectStatus().isForbidden();

    verifyNoInteractions(mockService);
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void update_withValidRequest_returnsUpdatedResponse() {
    when(mockService.update(eq(publicId), any(ExerciseRequest.class)))
        .thenReturn(savedResponse);

    client.put().uri("/api/v1/exercises/{publicId}", publicId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(validRequest)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExerciseResponse.class).value(response -> {
          assertThat(response.uid()).isEqualTo(publicId);
          assertThat(response.title()).isEqualTo("Bokstavenes rangering");
        });
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void update_withInvalidRequest_returns400BadRequest() {
    ExerciseRequest invalidRequest = new ExerciseRequest("", "", "", "", null);

    client.put().uri("/api/v1/exercises/{publicId}", publicId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(invalidRequest)
        .exchange()
        .expectStatus().isBadRequest();

    verifyNoInteractions(mockService);
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void update_withUnknownPublicId_returns404NotFound() {
    when(mockService.update(any(UUID.class), any(ExerciseRequest.class)))
        .thenThrow(new NotFoundException("Exercise not found"));

    client.put().uri("/api/v1/exercises/{publicId}", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .body(validRequest)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void delete_withValidPublicId_returns204NoContent() {
    client.delete().uri("/api/v1/exercises/{publicId}", publicId)
        .exchange()
        .expectStatus().isNoContent();

    verify(mockService).delete(publicId);
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void delete_withUnknownPublicId_returns404NotFound() {
    doThrow(new NotFoundException("Exercise not found"))
        .when(mockService).delete(any(UUID.class));

    client.delete().uri("/api/v1/exercises/{publicId}", UUID.randomUUID())
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void delete_withStudentRole_returnsForbiddenInWebMvcSlice() {
    client.delete().uri("/api/v1/exercises/{publicId}", publicId)
        .exchange()
        .expectStatus().isForbidden();

    verifyNoInteractions(mockService);
  }
}
