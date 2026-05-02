package no.safety.backend.completedexercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.service.CompletedExerciseService;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing completed exercises.
 * Provides endpoints for students to mark exercises as completed and retrieve their progress.
 * Teachers can also view completed exercises for students in their classroom.
 */
@RestController
@RequestMapping("/api/v1/completed-exercises")
@RequiredArgsConstructor
public class CompletedExerciseController {

  private final CompletedExerciseService completedExerciseService;
  private final StudentUserRepository studentUserRepository;

  /**
   * Marks an exercise as completed for the authenticated student.
   * Validates that the exercise is not locked before allowing completion.
   *
   * @param request the request containing the exercise ID to mark as completed
   * @param principal the authenticated user principal
   * @return the completed exercise response with student and exercise IDs
   */
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

  /**
   * Retrieves all completed exercises for the authenticated student.
   *
   * @param principal the authenticated user principal
   * @return list of completed exercise responses for the student
   */
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

  /**
   * Retrieves all completed exercises for a specific student.
   * This endpoint is intended for teachers to monitor classroom progress.
   *
   * @param studentUid the public UUID of the student
   * @return list of completed exercise responses for the specified student
   */
  @Operation(
      summary = "Get completed exercises for a student",
      description = "Returns all completed exercise rows for the selected student. Used by teachers for classroom progress.",
      tags = {"Completed exercises"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping("/students/{studentUid}")
  public List<CompletedExerciseResponse> getCompletedExercisesForStudent(
      @PathVariable UUID studentUid) {
    return completedExerciseService.getCompletedExercisesForStudent(studentUid);
  }

  /**
   * Retrieves the StudentUser entity for the authenticated principal.
   *
   * @param principal the authenticated user principal
   * @return the StudentUser entity
   * @throws NotFoundException if no student is found with the principal's public UUID
   */
  private StudentUser getStudent(UserPrincipal principal) {
    return studentUserRepository.findByPublicUuid(principal.getUserPublicUuid())
        .orElseThrow(() -> new NotFoundException("Student not found"));
  }
}
