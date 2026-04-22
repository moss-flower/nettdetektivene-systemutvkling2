package no.safety.backend.world.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.service.WorldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worlds")
@RequiredArgsConstructor
public class WorldController {

  private final WorldService worldService;

  @Operation(summary = "Get all worlds", tags = {"Worlds"})
  @GetMapping
  public List<WorldResponse> getAllWorlds() {
    return worldService.getAllWorlds();
  }

  @Operation(summary = "Get world by UID", tags = {"Worlds"})
  @GetMapping("/{uid}")
  public WorldResponse getWorldByUid(@PathVariable String uid) {
    return worldService.getWorldByUid(uid);
  }
}
