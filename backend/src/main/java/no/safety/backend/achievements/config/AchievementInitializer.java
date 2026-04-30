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

  private final AchievementRepository achievementRepository;

  @Override
  public void run(String... args) {
    createIfMissing(
        "Passordbanken",
        "Fullførte Passordbanken.",
        AchievementCategory.STOP,
        "Passordbanken",
        null
    );

    createIfMissing(
        "Sosiale møteplassen",
        "Fullførte Sosiale møteplassen.",
        AchievementCategory.STOP,
        "Sosiale møteplassen",
        null
    );

    createIfMissing(
        "Nyhetskvartalet",
        "Fullførte Nyhetskvartalet.",
        AchievementCategory.STOP,
        "Nyhetskvartalet",
        null
    );

    createIfMissing(
        "Markedsplassen",
        "Fullførte Markedsplassen.",
        AchievementCategory.STOP,
        "Markedsplassen",
        null
    );

    createIfMissing(
        "Postkontoret",
        "Fullførte Postkontoret.",
        AchievementCategory.STOP,
        "Postkontoret",
        null
    );

    createIfMissing(
        "Fotografen",
        "Fullførte Fotografen.",
        AchievementCategory.STOP,
        "Fotografen",
        null
    );

    createIfMissing(
        "Datasenteret",
        "Beseiret hovedbossen i Datasenteret.",
        AchievementCategory.SPECIAL,
        "Datasenteret",
        "Spesialmedalje for å ha slått bossen"
    );
  }

  private void createIfMissing(
      String title,
      String description,
      AchievementCategory category,
      String source,
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
    achievement.setReward(reward);

    achievementRepository.save(achievement);
  }
}
