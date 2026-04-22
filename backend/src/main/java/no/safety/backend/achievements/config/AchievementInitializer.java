package no.safety.backend.achievements.config;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.achievements.repository.AchievementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AchievementInitializer implements CommandLineRunner {

  private final AchievementRepository achievementRepository;

  @Override
  public void run(String... args) {
    createIfMissing(
        "COMPLETE_PASSWORD_BANK",
        "Passordbanken",
        "Fullførte Passordbanken.",
        AchievementCategory.STOP,
        "Passordbanken",
        "/images/medals/password-bank.png",
        null
    );

    createIfMissing(
        "COMPLETE_SOCIAL_SPACE",
        "Sosiale møteplassen",
        "Fullførte Sosiale møteplassen.",
        AchievementCategory.STOP,
        "Sosiale møteplassen",
        "/images/medals/social-space.png",
        null
    );

    createIfMissing(
        "COMPLETE_NEWS_SQUARE",
        "Nyhetskvartalet",
        "Fullførte Nyhetskvartalet.",
        AchievementCategory.STOP,
        "Nyhetskvartalet",
        "/images/medals/news-square.png",
        null
    );

    createIfMissing(
        "COMPLETE_MARKETPLACE",
        "Markedsplassen",
        "Fullførte Markedsplassen.",
        AchievementCategory.STOP,
        "Markedsplassen",
        "/images/medals/marketplace.png",
        null
    );

    createIfMissing(
        "COMPLETE_POST_OFFICE",
        "Postkontoret",
        "Fullførte Postkontoret.",
        AchievementCategory.STOP,
        "Postkontoret",
        "/images/medals/post-office.png",
        null
    );

    createIfMissing(
        "COMPLETE_PHOTOGRAPHER",
        "Fotografen",
        "Fullførte Fotografen.",
        AchievementCategory.STOP,
        "Fotografen",
        "/images/medals/photographer.png",
        null
    );

    createIfMissing(
        "BEAT_FINAL_BOSS",
        "Datasenteret",
        "Beseiret hovedbossen i Datasenteret.",
        AchievementCategory.SPECIAL,
        "Datasenteret",
        "/images/medals/final-boss.png",
        "Spesialmedalje for å ha slått bossen"
    );
  }

  private void createIfMissing(
      String code,
      String title,
      String description,
      AchievementCategory category,
      String source,
      String imageUrl,
      String reward
  ) {
    if (achievementRepository.findByCode(code).isPresent()) {
      return;
    }

    Achievement achievement = new Achievement();
    achievement.setCode(code);
    achievement.setTitle(title);
    achievement.setDescription(description);
    achievement.setCategory(category);
    achievement.setSource(source);
    achievement.setImageUrl(imageUrl);
    achievement.setReward(reward);

    achievementRepository.save(achievement);
  }
}