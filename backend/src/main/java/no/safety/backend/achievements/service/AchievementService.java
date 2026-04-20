package no.safety.backend.achievements.service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.UserAchievement;
import no.safety.backend.achievements.repository.AchievementRepository;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.user.model.StudentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final UserAchievementRepository userAchievementRepository;

  /**
   * Attempts to unlock one achievement for a student.
   *
   * @param student the student who may unlock the achievement
   * @param achievementCode the stable achievement code, e.g. COMPLETE_POST_OFFICE
   * @return true if a new unlock was created, false if the student already had it
   */
  @Transactional
  public boolean unlockAchievement(StudentUser student, String achievementCode) {
    Achievement achievement = achievementRepository.findByCode(achievementCode)
        .orElseThrow(() ->
            new IllegalArgumentException("Unknown achievement code: " + achievementCode));

    boolean alreadyUnlocked =
        userAchievementRepository.existsByStudentAndAchievement(student, achievement);

    if (alreadyUnlocked) {
      return false;
    }

    UserAchievement userAchievement = new UserAchievement();
    userAchievement.setStudent(student);
    userAchievement.setAchievement(achievement);
    userAchievement.setUnlockedAt(Instant.now());

    userAchievementRepository.save(userAchievement);
    return true;
  }

  @Transactional(readOnly = true)
  public List<AchievementResponse> getAchievementsForStudent(StudentUser student) {
    List<UserAchievement> unlockedAchievements = userAchievementRepository.findByStudent(student);

    return unlockedAchievements.stream()
        .map(userAchievement -> AchievementResponse.builder()
            .id(String.valueOf(userAchievement.getAchievement().getId()))
            .title(userAchievement.getAchievement().getTitle())
            .description(userAchievement.getAchievement().getDescription())
            .category(userAchievement.getAchievement().getCategory().name().toLowerCase())
            .source(userAchievement.getAchievement().getSource())
            .unlocked(true)
            .imageUrl(userAchievement.getAchievement().getImageUrl())
            .reward(userAchievement.getAchievement().getReward())
            .build())
        .toList();
  }

}