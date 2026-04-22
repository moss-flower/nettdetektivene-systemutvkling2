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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final UserAchievementRepository userAchievementRepository;

  /**
   * Attempts to unlock one achievement for a student.
   *
   * @param student         the student who may unlock the achievement
   * @param achievementCode the stable achievement code, e.g. COMPLETE_POST_OFFICE
   * @return true if a new unlock was created, false if the student already had it
   */
  @Transactional
  public boolean unlockAchievement(StudentUser student, String achievementCode) {
    Achievement achievement = achievementRepository.findByCode(achievementCode)
        .orElseThrow(() -> new IllegalArgumentException("Unknown achievement code: " + achievementCode));

    boolean alreadyUnlocked = userAchievementRepository.existsByStudentAndAchievement(student, achievement);

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
  public List<AchievementResponse> getAchievementsForStudent(StudentUser studentUser) {
    List<Achievement> allAchievements = achievementRepository.findAll();
    List<UserAchievement> unlockedAchievements = userAchievementRepository.findByStudent(studentUser);

    Set<Long> unlockedAchievementIds = unlockedAchievements.stream()
        .map(userAchievement -> userAchievement.getAchievement().getId())
        .collect(Collectors.toSet());

    return allAchievements.stream()
        .map(achievement -> AchievementResponse.builder()
            .id(String.valueOf(achievement.getId()))
            .title(achievement.getTitle())
            .description(achievement.getDescription())
            .category(achievement.getCategory().name().toLowerCase())
            .source(achievement.getSource())
            .unlocked(unlockedAchievementIds.contains(achievement.getId()))
            .imageUrl(achievement.getImageUrl())
            .reward(achievement.getReward())
            .build())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AchievementResponse> getAllAchievements() {
    return achievementRepository.findAll().stream()
        .map(achievement -> AchievementResponse.builder()
            .id(String.valueOf(achievement.getId()))
            .title(achievement.getTitle())
            .description(achievement.getDescription())
            .category(achievement.getCategory().name().toLowerCase())
            .source(achievement.getSource())
            .unlocked(false)
            .imageUrl(achievement.getImageUrl())
            .reward(achievement.getReward())
            .build())
        .toList();
  }

}