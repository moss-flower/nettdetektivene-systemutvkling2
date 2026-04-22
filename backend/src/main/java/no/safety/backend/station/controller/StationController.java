package no.safety.backend.station.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

  private final StationService stationService;

  @Operation(summary = "Get all stations", tags = {"Stations"})
  @GetMapping
  public List<StationResponse> getAllStations() {
    return stationService.getAllStations();
  }

  @Operation(summary = "Get station by UID", tags = {"Stations"})
  @GetMapping("/{uid}")
  public StationResponse getStationByUid(@PathVariable String uid) {
    return stationService.getStationByUid(uid);
  }
}
