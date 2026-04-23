package no.safety.backend.achievements.config;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.achievements.repository.AchievementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AchievementInitializer implements CommandLineRunner {

  private static final String MEDAL_IMAGE_URL = "/images/medal.png";

  private final AchievementRepository achievementRepository;

  @Override
  public void run(String... args) {
    createIfMissing(
        "Passordbanken",
        "Fullførte Passordbanken.",
        AchievementCategory.STOP,
        "Passordbanken",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Sosiale møteplassen",
        "Fullførte Sosiale møteplassen.",
        AchievementCategory.STOP,
        "Sosiale møteplassen",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Nyhetskvartalet",
        "Fullførte Nyhetskvartalet.",
        AchievementCategory.STOP,
        "Nyhetskvartalet",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Markedsplassen",
        "Fullførte Markedsplassen.",
        AchievementCategory.STOP,
        "Markedsplassen",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Postkontoret",
        "Fullførte Postkontoret.",
        AchievementCategory.STOP,
        "Postkontoret",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Fotografen",
        "Fullførte Fotografen.",
        AchievementCategory.STOP,
        "Fotografen",
        MEDAL_IMAGE_URL,
        null
    );

    createIfMissing(
        "Datasenteret",
        "Beseiret hovedbossen i Datasenteret.",
        AchievementCategory.SPECIAL,
        "Datasenteret",
        MEDAL_IMAGE_URL,
        "Spesialmedalje for å ha slått bossen"
    );
  }

  private void createIfMissing(
      String title,
      String description,
      AchievementCategory category,
      String source,
      String imageUrl,
      String reward
  ) {
    if (achievementRepository.findByTitle(title).isPresent()) {
      return;
    }

    Achievement achievement = new Achievement();
    achievement.setUUID(UUID.randomUUID());
    achievement.setTitle(title);
    achievement.setDescription(description);
    achievement.setCategory(category);
    achievement.setSource(source);
    achievement.setImageUrl(imageUrl);
    achievement.setReward(reward);

    achievementRepository.save(achievement);
  }
}