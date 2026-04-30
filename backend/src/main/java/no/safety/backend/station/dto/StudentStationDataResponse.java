package no.safety.backend.station.dto;

import java.util.List;
import no.safety.backend.world.dto.WorldResponse;

public record StudentStationDataResponse(
    WorldResponse world,
    List<StationDataResponse> stations
) {
}
