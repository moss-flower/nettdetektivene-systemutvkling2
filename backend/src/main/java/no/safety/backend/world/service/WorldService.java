package no.safety.backend.world.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorldService {

  private final WorldRepository worldRepository;

  @Transactional(readOnly = true)
  public List<WorldResponse> getAllWorlds() {
    return worldRepository.findAll().stream()
        .map(WorldResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public WorldResponse getWorldByUid(UUID uid) {
    return worldRepository.findById(uid)
        .map(WorldResponse::from)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + uid));
  }
}
