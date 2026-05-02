package no.safety.backend.world.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.world.dto.WorldRequest;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.model.World;
import no.safety.backend.world.service.WorldService;
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
 * A REST Controller that defines world handling API endpoints. All mutating endpoints require
 * the teacher role.
 */
@RestController
@RequestMapping("/api/v1/worlds")
@RequiredArgsConstructor
public class WorldController {

  private final WorldService worldService;

  /**
   * Creates a new {@link World}
   *
   * @param request A {@link WorldRequest} containing data about the world
   * @return {@link WorldResponse} containing information about the created world
   */
  @Operation(summary = "Create a new world", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorldResponse create(@Valid @RequestBody WorldRequest request) {
    return worldService.create(request);
  }

  /**
   * Lists all {@link World}s.
   *
   * @return a list of {@link WorldResponse}s
   */
  @Operation(summary = "Get all worlds", tags = {"Worlds"})
  @GetMapping
  public List<WorldResponse> getAllWorlds() {
    return worldService.getAllWorlds();
  }

  /**
   * Retrieves a {@link World} by its uid.
   *
   * @param uid the {@link UUID} of the {@link World} to retrieve
   * @return a {@link WorldResponse}
   * @throws NotFoundException if a {@link World} with the given uid could not be found.
   */
  @Operation(summary = "Get world by UID", tags = {"Worlds"})
  @GetMapping("/{uid}")
  public WorldResponse getWorldByUid(@PathVariable UUID uid) {
    return worldService.getWorldByUid(uid);
  }

  /**
   * Updates a {@link World}.
   *
   * @param uid     {@link UUID} of the {@link World} to update
   * @param request a {@link WorldRequest} containing the new {@link World} data
   * @return a {@link WorldResponse}
   */
  @Operation(summary = "Update world by UID", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{uid}")
  public WorldResponse updateWorldByUid(@PathVariable UUID uid, @Valid @RequestBody WorldRequest request) {
    return worldService.update(uid, request);
  }

  /**
   * Deletes a {@link World}.
   *
   * @param uid {@link UUID} of the world to delete.
   * @return a 201 No Content {@link ResponseEntity}
   */
  @Operation(summary = "Delete world by UID", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{uid}")
  public ResponseEntity<Void> deleteWorldByUid(@PathVariable UUID uid) {
    worldService.delete(uid);
    return ResponseEntity.noContent().build();
  }
}
