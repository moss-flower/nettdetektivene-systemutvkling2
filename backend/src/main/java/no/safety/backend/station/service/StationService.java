package no.safety.backend.station.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.station.dto.StationRequest;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StationService {

  private final StationRepository stationRepository;

  @Transactional
  public StationResponse create(StationRequest request) {
    Station station = new Station();

    applyFields(station, request);
    return StationResponse.from(stationRepository.save(station));
  }

  @Transactional(readOnly = true)
  public List<StationResponse> getAllStations() {
    return stationRepository.findAll().stream()
        .map(StationResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public StationResponse getStationByUid(UUID uid) {
    return stationRepository.findByUid(uid)
        .map(StationResponse::from)
        .orElseThrow(() -> new NotFoundException("Station not found with uid: " + uid));
  }

  @Transactional
  public StationResponse update(UUID uid, StationRequest request) {
    Station station = require(uid);

    applyFields(station, request);
    return StationResponse.from(station);
  }

  @Transactional
  public void delete(UUID uid) {
    stationRepository.delete(require(uid));
  }

  private void applyFields(Station station, StationRequest request) {
    station.setTitle(request.title());
    station.setDescription(request.description());
    station.setImage(request.image());
    station.setBackgroundColor(request.backgroundColor());
    station.setExerciseUids(request.exerciseUids());
  }

  private Station require(UUID uid) {
    return stationRepository.findByUid(uid)
        .orElseThrow(() -> new NotFoundException("Station not found with uid: " + uid));
  }
}
