package no.safety.backend.station.dto;

import java.util.List;
import no.safety.backend.station.model.Station;

public record StationResponse(
    String uid,
    String title,
    String description,
    String image,
    String backgroundColor,
    List<String> exerciseUids
) {
  public static StationResponse from(Station station) {
    return new StationResponse(
        station.getUid(),
        station.getTitle(),
        station.getDescription(),
        station.getImage(),
        station.getBackgroundColor(),
        station.getExerciseUids()
    );
  }
}
