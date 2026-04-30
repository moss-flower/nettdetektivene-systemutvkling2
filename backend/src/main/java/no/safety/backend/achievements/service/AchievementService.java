package no.safety.backend.achievements.service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.UserAchievement;
import no.safety.backend.achievements.repository.AchievementRepository;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.user.model.StudentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final UserAchievementRepository userAchievementRepository;

  @Transactional(readOnly = true)
  public List<AchievementResponse> getAchievementsForStudent(StudentUser student) {
    List<Achievement> allAchievements = achievementRepository.findAll();
    List<UserAchievement> unlockedAchievements = userAchievementRepository.findByStudent(student);

    Set<Long> unlockedAchievementIds = unlockedAchievements.stream()
        .map(userAchievement -> userAchievement.getAchievement().getInternalId())
        .collect(Collectors.toSet());

    return allAchievements.stream()
        .map(achievement -> AchievementResponse.builder()
            .UUID(achievement.getUUID())
            .title(achievement.getTitle())
            .description(achievement.getDescription())
            .category(achievement.getCategory().name().toLowerCase())
            .source(achievement.getSource())
            .unlocked(unlockedAchievementIds.contains(achievement.getInternalId()))
            .reward(achievement.getReward())
            .build())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AchievementResponse> getAllAchievements() {
    return achievementRepository.findAll().stream()
        .map(achievement -> achievementToResponse(achievement, false))
        .toList();
  }

  @Transactional(readOnly = true)
  public AchievementResponse getAchievementByUUID(UUID achievementUUID) {
    Achievement achievement = achievementRepository.findByUUID(achievementUUID)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Achievement not found"
        ));

    return achievementToResponse(achievement, false);
  }

  @Transactional
  public AchievementResponse createAchievement(AchievementRequest request) {
    Achievement achievement = new Achievement();
    achievement.setUUID(UUID.randomUUID());
    achievement.setTitle(request.getTitle());
    achievement.setDescription(request.getDescription());
    achievement.setCategory(request.getCategory());
    achievement.setSource(request.getSource());
    achievement.setReward(request.getReward());

    Achievement saved = achievementRepository.save(achievement);
    return achievementToResponse(saved, false);
  }

  @Transactional
  public AchievementResponse updateAchievement(UUID achievementUUID, AchievementRequest request) {
    Achievement achievement = achievementRepository.findByUUID(achievementUUID)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Achievement not found"
        ));

    achievement.setTitle(request.getTitle());
    achievement.setDescription(request.getDescription());
    achievement.setCategory(request.getCategory());
    achievement.setSource(request.getSource());
    achievement.setReward(request.getReward());

    Achievement saved = achievementRepository.save(achievement);
    return achievementToResponse(saved, false);
  }

  @Transactional
  public void deleteAchievement(UUID achievementUUID) {
    Achievement achievement = achievementRepository.findByUUID(achievementUUID)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Achievement not found"
        ));

    achievementRepository.delete(achievement);
  }

  private AchievementResponse achievementToResponse(Achievement achievement, boolean unlocked) {
    return AchievementResponse.builder()
        .UUID(achievement.getUUID())
        .title(achievement.getTitle())
        .description(achievement.getDescription())
        .category(achievement.getCategory().name().toLowerCase())
        .source(achievement.getSource())
        .unlocked(unlocked)
        .reward(achievement.getReward())
        .build();
  }
}
