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
 * Exercise controller that defines API endpoints for managing exercises.
 */
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise management")
public class ExerciseController {

  private final ExerciseService exerciseService;

  /**
   * Creates a new exercise.
   *
   * @param request an {@link ExerciseResponse} containing exercise information and tasks.
   * @return the {@link ExerciseResponse} of the created exercise.
   * @throws org.springframework.web.client.HttpClientErrorException.BadRequest   if the request is
   *                                                                              invalid
   * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized if the caller is
   *                                                                              not a teacher
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
   * Retrieves an exercise by its public ID.
   *
   * @param publicId the {@link UUID} of the exercise
   * @return an {@link ExerciseResponse} containing information about the exercise.
   * @throws no.safety.backend.exception.NotFoundException if an exercise with the public ID was not
   *                                                       found.
   */
  @Operation(
      summary = "Get an exercise",
      description = "Gets an exercise by its public UUID."
  )
  @GetMapping("/{publicId}")
  public ExerciseResponse get(@PathVariable UUID publicId) {
    return exerciseService.get(publicId);
  }

  /**
   * Retrieves a paginated list of all exercises
   *
   * @param req a {@link ExerciseReadRequest} containing pagination and sorting information.
   * @return a {@link Page} of exercise entities.
   * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized if the caller is
   *                                                                              not a teacher
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
   * Updates every field of an exercise except for the internal and public ID.
   *
   * @param publicId the {@link UUID} of the exercise to update.
   * @param request  an {@link ExerciseRequest} containing the new exercise data.
   * @return an {@link ExerciseResponse} containing the updated exercise data.
   * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized if the caller is
   *                                                                              not a teacher
   * @throws no.safety.backend.exception.NotFoundException                        if an exercise
   *                                                                              with the public ID
   *                                                                              was not found.
   */
  @Operation(
      summary = "Update an exercise",
      description = "Replaces an exercise's fields and tasks. Existing tasks are removed and replaced with the submitted set."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{publicId}")
  public ExerciseResponse update(
      @PathVariable UUID publicId,
      @Valid @RequestBody ExerciseRequest request) {
    return exerciseService.update(publicId, request);
  }

  /**
   * Deletes an exercise by its public ID.
   *
   * @param publicId  the {@link UUID} of the exercise to delete.
   * @return an empty 204 No Content response.
   * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized if the caller is
   *                                                                              not a teacher
   * @throws no.safety.backend.exception.NotFoundException                        if an exercise
   *                                                                              with the public ID
   *                                                                              was not found.
   */
  @Operation(
      summary = "Delete an exercise",
      description = "Deletes an exercise and its tasks."
  )
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{publicId}")
  public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
    exerciseService.delete(publicId);
    return ResponseEntity.noContent().build();
  }
}
