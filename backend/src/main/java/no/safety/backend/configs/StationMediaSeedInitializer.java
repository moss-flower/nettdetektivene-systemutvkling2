package no.safety.backend.configs;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.media.service.MediaStorageService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@Order(4)
@RequiredArgsConstructor
public class StationMediaSeedInitializer implements ApplicationRunner {

  private static final String PNG = "image/png";

  private static final String NEWSSTAND_STATION_IMAGE = "seed/stations/nyhetskvtartalet.png";
  private static final String PHOTOGRAPH_STATION_IMAGE = "seed/stations/fotografen.png";
  private static final String POSTOFFICE_STATION_IMAGE = "seed/stations/postkontoret.png";
  private static final String PASSWORDBANK_STATION_IMAGE = "seed/stations/passordbanken.png";
  private static final String MARKETPLACE_STATION_IMAGE = "seed/stations/markedsplassen.png";
  private static final String SOCIALMEDIA_STATION_IMAGE = "seed/stations/sosial-moteplassen.png";

  private final MediaStorageService mediaStorageService;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedStationImages();
  }

  private void seedStationImages() {
    List.of(
        NEWSSTAND_STATION_IMAGE,
        PHOTOGRAPH_STATION_IMAGE,
        POSTOFFICE_STATION_IMAGE,
        PASSWORDBANK_STATION_IMAGE,
        MARKETPLACE_STATION_IMAGE,
        SOCIALMEDIA_STATION_IMAGE
    ).forEach(image -> mediaStorageService.uploadPublicClasspathImage(image, image, PNG));
  }
}
