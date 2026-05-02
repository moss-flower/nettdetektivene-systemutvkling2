package no.safety.backend.excercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.excercise.dto.ExerciseReadRequest;
import no.safety.backend.excercise.dto.ExerciseRequest;
import no.safety.backend.excercise.dto.ExerciseResponse;
import no.safety.backend.excercise.service.ExerciseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing exercises.
 * Provides endpoints for creating, reading, updating, and deleting exercises.
 * All write operations require TEACHER role.
 */
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise management")
public class ExerciseController {

  private final ExerciseService exerciseService;

  /**
   * Creates a new exercise with the provided tasks.
   *
   * @param request the exercise request containing title, difficulty, tasks, etc.
   * @return the created exercise response
   */
  @Operation(
      summary = "Create a new exercise",
      description = "Creates a new exercise with its tasks."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ExerciseResponse create(@Valid @RequestBody ExerciseRequest request) {
    return exerciseService.create(request);
  }

  /**
   * Retrieves a single exercise by its public UUID.
   *
   * @param uid the public UUID of the exercise to retrieve
   * @return the exercise response containing all exercise details
   */
  @Operation(
      summary = "Get an exercise",
      description = "Gets an exercise by its UUID."
  )
  @GetMapping("/{uid}")
  public ExerciseResponse get(@PathVariable UUID uid) {
    return exerciseService.get(uid);
  }

  /**
   * Retrieves a paginated list of all exercises.
   *
   * @param req the read request containing pagination parameters (page, size)
   * @return a page of exercise responses
   */
  @Operation(
      summary = "Get all exercises",
      description = "Gets a paginated list of all exercises. Accepts standard Spring Data page/size/sort query params."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping
  public Page<ExerciseResponse> getAll(@Valid ExerciseReadRequest req) {
    return exerciseService.list(
        req);
  }

  /**
   * Updates an existing exercise by replacing all fields and tasks.
   * Existing tasks are removed and replaced with the new set.
   *
   * @param uid the public UUID of the exercise to update
   * @param request the exercise request containing updated fields and tasks
   * @return the updated exercise response
   */
  @Operation(
      summary = "Update an exercise",
      description = "Replaces an exercise's fields and tasks. Existing tasks are removed and replaced with the submitted set."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{uid}")
  public ExerciseResponse update(
      @PathVariable UUID uid,
      @Valid @RequestBody ExerciseRequest request) {
    return exerciseService.update(uid, request);
  }

  /**
   * Deletes an exercise and all its associated tasks.
   *
   * @param uid the public UUID of the exercise to delete
   * @return HTTP 204 No Content on successful deletion
   */
  @Operation(
      summary = "Delete an exercise",
      description = "Deletes an exercise and its tasks."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{uid}")
  public ResponseEntity<Void> delete(@PathVariable UUID uid) {
    exerciseService.delete(uid);
    return ResponseEntity.noContent().build();
  }
}
