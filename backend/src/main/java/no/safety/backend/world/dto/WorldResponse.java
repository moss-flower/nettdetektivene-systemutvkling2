package no.safety.backend.world.dto;

import java.util.List;
import java.util.UUID;
import no.safety.backend.world.model.World;

public record WorldResponse(
    UUID uid,
    String description,
    String image,
    List<UUID> stationUids
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
