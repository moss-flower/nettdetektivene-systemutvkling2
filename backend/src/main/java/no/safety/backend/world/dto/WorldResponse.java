package no.safety.backend.world.dto;

import java.util.List;
import no.safety.backend.world.model.World;

public record WorldResponse(
    String uid,
    String description,
    String image,
    List<String> stationUids
) {
  public static WorldResponse from(World world) {
    return new WorldResponse(
        world.getUid(),
        world.getDescription(),
        world.getImage(),
        world.getStationUids()
    );
  }
}
