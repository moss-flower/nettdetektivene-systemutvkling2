package no.safety.backend.world.dto;

import java.util.List;
import java.util.UUID;
import no.safety.backend.world.model.World;

/**
 * Response DTO for {@link World}s.
 *
 * @param uid         {@link UUID} of the {@link World}
 * @param title       title of the {@link World}
 * @param description description of the {@link World}
 * @param image       a {@link World} image
 * @param stationUids uids of all stations the world contains as a {@link List} of {@link UUID}
 */
public record WorldResponse(
    UUID uid,
    String title,
    String description,
    String image,
    List<UUID> stationUids
) {

  /**
   * Generates a {@link WorldResponse} from a {@link World} entity.
   *
   * @param world {@link World} to make a response from
   * @return {@link WorldResponse} containing information about the {@link World}
   */
  public static WorldResponse from(World world) {
    return new WorldResponse(
        world.getUid(),
        world.getTitle(),
        world.getDescription(),
        world.getImage(),
        world.getStationUids()
    );
  }
}
