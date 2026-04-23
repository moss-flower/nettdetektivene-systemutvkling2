package no.safety.backend.completedexercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.service.CompletedExerciseService;
import no.safety.backend.user.model.StudentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/completed-exercises")
@RequiredArgsConstructor
public class CompletedExerciseController {

  private final CompletedExerciseService completedExerciseService;

  @Operation(
      summary = "Mark an exercise as completed",
      description = "Creates a completed exercise row for the logged-in student and the provided exercise ID.",
      tags = {"Completed exercises"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CompletedExerciseResponse create(
      @Valid @RequestBody CompletedExerciseRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return completedExerciseService.create(request, getStudent(principal));
  }

  @Operation(
      summary = "Get my completed exercises",
      description = "Returns all completed exercise rows for the logged-in student.",
      tags = {"Completed exercises"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/me")
  public List<CompletedExerciseResponse> getMyCompletedExercises(
      @AuthenticationPrincipal UserPrincipal principal) {
    return completedExerciseService.getMyCompletedExercises(getStudent(principal));
  }

  private StudentUser getStudent(UserPrincipal principal) {
    return (StudentUser) principal.user();
  }
}
