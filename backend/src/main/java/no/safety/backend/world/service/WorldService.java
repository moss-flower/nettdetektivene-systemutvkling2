package no.safety.backend.world.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.world.dto.WorldRequest;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorldService {

  private final WorldRepository worldRepository;

  @Transactional
  public WorldResponse create(WorldRequest request) {
    World world = new World();

    applyFields(world, request);
    return WorldResponse.from(worldRepository.save(world));
  }

  @Transactional(readOnly = true)
  public List<WorldResponse> getAllWorlds() {
    return worldRepository.findAll().stream()
        .map(WorldResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public WorldResponse getWorldByUid(UUID uid) {
    return worldRepository.findByUid(uid)
        .map(WorldResponse::from)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + uid));
  }

  @Transactional
  public WorldResponse update(UUID uid, WorldRequest request) {
    World world = require(uid);

    applyFields(world, request);
    return WorldResponse.from(world);
  }

  @Transactional
  public void delete(UUID uid) {
    worldRepository.delete(require(uid));
  }

  private void applyFields(World world, WorldRequest request) {
    world.setTitle(request.title());
    world.setDescription(request.description());
    world.setImage(request.image());
    world.setStationUids(request.stationUids());
  }

  private World require(UUID uid) {
    return worldRepository.findByUid(uid)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + uid));
  }
}
