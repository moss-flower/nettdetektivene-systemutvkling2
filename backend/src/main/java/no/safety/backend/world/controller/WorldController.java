package no.safety.backend.world.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.world.dto.WorldRequest;
import no.safety.backend.world.dto.WorldResponse;
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

@RestController
@RequestMapping("/api/v1/worlds")
@RequiredArgsConstructor
public class WorldController {

  private final WorldService worldService;

  @Operation(summary = "Create a new world", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorldResponse create(@Valid @RequestBody WorldRequest request) {
    return worldService.create(request);
  }

  @Operation(summary = "Get all worlds", tags = {"Worlds"})
  @GetMapping
  public List<WorldResponse> getAllWorlds() {
    return worldService.getAllWorlds();
  }

  @Operation(summary = "Get world by UID", tags = {"Worlds"})
  @GetMapping("/{uid}")
  public WorldResponse getWorldByUid(@PathVariable UUID uid) {
    return worldService.getWorldByUid(uid);
  }

  @Operation(summary = "Update world by UID", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{uid}")
  public WorldResponse updateWorldByUid(@PathVariable UUID uid, @Valid @RequestBody WorldRequest request) {
    return worldService.update(uid, request);
  }

  @Operation(summary = "Delete world by UID", tags = {"Worlds"})
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{uid}")
  public ResponseEntity<Void> deleteWorldByUid(@PathVariable UUID uid) {
    worldService.delete(uid);
    return ResponseEntity.noContent().build();
  }
}
