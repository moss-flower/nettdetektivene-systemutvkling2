package no.safety.backend.station.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StationService {

  private final StationRepository stationRepository;

  @Transactional(readOnly = true)
  public List<StationResponse> getAllStations() {
    return stationRepository.findAll().stream()
        .map(StationResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public StationResponse getStationByUid(UUID uid) {
    return stationRepository.findById(uid)
        .map(StationResponse::from)
        .orElseThrow(() -> new NotFoundException("Station not found with uid: " + uid));
  }
}
