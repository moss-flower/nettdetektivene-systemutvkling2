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

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise management")
public class ExerciseController {

  private final ExerciseService exerciseService;

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

  @Operation(
      summary = "Get an exercise",
      description = "Gets an exercise by its UUID."
  )
  @GetMapping("/{uid}")
  public ExerciseResponse get(@PathVariable UUID uid) {
    return exerciseService.get(uid);
  }

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
