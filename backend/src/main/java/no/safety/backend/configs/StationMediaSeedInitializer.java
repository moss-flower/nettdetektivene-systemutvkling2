package no.safety.backend.configs;

import java.util.List;
import java.util.Locale;
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

  private static final String NEWSSTAND_STATION_IMAGE = "seed/stations/nyhetskvtartalet.png";
  private static final String PHOTOGRAPH_STATION_IMAGE = "seed/stations/fotografen.png";
  private static final String POSTOFFICE_STATION_IMAGE = "seed/stations/postkontoret.png";
  private static final String PASSWORDBANK_STATION_IMAGE = "seed/stations/passordbanken.png";
  private static final String MARKETPLACE_STATION_IMAGE = "seed/stations/markedsplassen.png";
  private static final String SOCIALMEDIA_STATION_IMAGE = "seed/stations/sosial-moteplassen.png";

  private static final String NEWSSTAND_SAFE_LIBRARY_IMAGE = "seed/tasks/nyhetskvartalet/safe-lesehjorne.png";
  private static final String NEWSSTAND_SAFE_READING_IMAGE = "seed/tasks/nyhetskvartalet/safe-barn-leser-mer.png";
  private static final String NEWSSTAND_WARNING_PHONE_IMAGE = "seed/tasks/nyhetskvartalet/warning-gratis-mobil.png";
  private static final String NEWSSTAND_WARNING_SCHOOL_IMAGE = "seed/tasks/nyhetskvartalet/warning-skoler-stenger.png";
  private static final String NEWSSTAND_WARNING_PLAYGROUND_IMAGE = "seed/tasks/nyhetskvartalet/warning-bot-lekeplass.png";

  private static final String PHOTOGRAPH_REAL_DOG_IMAGE = "seed/tasks/fotografen/real-dog-frisbee.png";
  private static final String PHOTOGRAPH_REAL_FOOTBALL_IMAGE = "seed/tasks/fotografen/real-football-player-kicking-ball.jpeg";
  private static final String PHOTOGRAPH_REAL_PEOPLE_IMAGE = "seed/tasks/fotografen/real-people-holding-hands.png";
  private static final String PHOTOGRAPH_AI_HANDS_IMAGE = "seed/tasks/fotografen/ai-folded-hands-many-fingers.jpeg";
  private static final String PHOTOGRAPH_AI_BANANA_IMAGE = "seed/tasks/fotografen/ai-kid-giant-banana.jpeg";
  private static final String PHOTOGRAPH_AI_SIGNS_IMAGE = "seed/tasks/fotografen/ai-road-sign-arrows-both-ways.png";
  private static final String PHOTOGRAPH_AI_MARS_IMAGE = "seed/tasks/fotografen/ai-eiffel-tower-mars-landscape.jpeg";
  private static final String PHOTOGRAPH_AI_SKY_IMAGE = "seed/tasks/fotografen/ai-sunset-spiral-clouds.jpeg";

  private static final String POSTOFFICE_FAKE_IPHONE_IMAGE = "seed/tasks/postkontoret/fake-you-won-iphone.jpeg";
  private static final String POSTOFFICE_FAKE_ACCOUNT_IMAGE = "seed/tasks/postkontoret/fake-your-account-will-be-closed.jpeg";
  private static final String POSTOFFICE_REAL_BOOK_IMAGE = "seed/tasks/postkontoret/real-book-reservation-reminder.png";
  private static final String POSTOFFICE_REAL_HIKE_IMAGE = "seed/tasks/postkontoret/real-hike-reminder.jpeg";
  private static final String POSTOFFICE_REAL_TIMETABLE_IMAGE = "seed/tasks/postkontoret/real-timeplan-next-week.png";
  private static final String POSTOFFICE_REAL_LIBRARY_IMAGE = "seed/tasks/postkontoret/real-welcome-to-library.jpeg";

  private static final String MARKETPLACE_FAKE_BIKE_IMAGE = "seed/tasks/markedsplassen/fake-bike-ad.jpeg";
  private static final String MARKETPLACE_FAKE_IPHONE_IMAGE = "seed/tasks/markedsplassen/fake-iphone-ad.jpeg";
  private static final String MARKETPLACE_FAKE_LAPTOP_IMAGE = "seed/tasks/markedsplassen/fake-laptop-ad.jpeg";
  private static final String MARKETPLACE_REAL_BIKE_IMAGE = "seed/tasks/markedsplassen/real-bike-ad.jpeg";
  private static final String MARKETPLACE_REAL_LAPTOP_IMAGE = "seed/tasks/markedsplassen/real-laptop-ad.jpeg";

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
        SOCIALMEDIA_STATION_IMAGE,
        NEWSSTAND_SAFE_LIBRARY_IMAGE,
        NEWSSTAND_SAFE_READING_IMAGE,
        NEWSSTAND_WARNING_PHONE_IMAGE,
        NEWSSTAND_WARNING_SCHOOL_IMAGE,
        NEWSSTAND_WARNING_PLAYGROUND_IMAGE,
        PHOTOGRAPH_REAL_DOG_IMAGE,
        PHOTOGRAPH_REAL_FOOTBALL_IMAGE,
        PHOTOGRAPH_REAL_PEOPLE_IMAGE,
        PHOTOGRAPH_AI_HANDS_IMAGE,
        PHOTOGRAPH_AI_BANANA_IMAGE,
        PHOTOGRAPH_AI_SIGNS_IMAGE,
        PHOTOGRAPH_AI_MARS_IMAGE,
        PHOTOGRAPH_AI_SKY_IMAGE,
        POSTOFFICE_FAKE_IPHONE_IMAGE,
        POSTOFFICE_FAKE_ACCOUNT_IMAGE,
        POSTOFFICE_REAL_BOOK_IMAGE,
        POSTOFFICE_REAL_HIKE_IMAGE,
        POSTOFFICE_REAL_TIMETABLE_IMAGE,
        POSTOFFICE_REAL_LIBRARY_IMAGE,
        MARKETPLACE_FAKE_BIKE_IMAGE,
        MARKETPLACE_FAKE_IPHONE_IMAGE,
        MARKETPLACE_FAKE_LAPTOP_IMAGE,
        MARKETPLACE_REAL_BIKE_IMAGE,
        MARKETPLACE_REAL_LAPTOP_IMAGE
    ).forEach(image -> mediaStorageService.uploadPublicClasspathImage(image, image, contentTypeFor(image)));
  }

  private String contentTypeFor(String path) {
    String normalizedPath = path.toLowerCase(Locale.ROOT);
    if (normalizedPath.endsWith(".png")) {
      return "image/png";
    }

    if (normalizedPath.endsWith(".jpg") || normalizedPath.endsWith(".jpeg")) {
      return "image/jpeg";
    }

    throw new IllegalStateException("Unsupported seed media type: " + path);
  }
}
