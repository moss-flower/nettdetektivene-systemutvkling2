package no.safety.backend.achievements.service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.achievements.model.UserAchievement;
import no.safety.backend.achievements.repository.AchievementRepository;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.notification.service.NotificationService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import no.safety.backend.station.model.Station;

@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final UserAchievementRepository userAchievementRepository;
  private final StudentUserRepository studentUserRepository;

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

  @Transactional
  public AchievementResponse unlockAchievementForStudent(StudentUser student, String source, AchievementCategory category) {
    Achievement achievement = achievementRepository.findBySourceAndCategory(source, category)
        .orElseThrow(() -> new NotFoundException("Achievement not found for source: " + source));

    return unlockAchievementForStudent(student, achievement);
  }

  @Transactional
  public AchievementResponse unlockStationAchievement(StudentUser student, Station station) {
    Achievement achievement = syncStationAchievement(station, null);
    return unlockAchievementForStudent(student, achievement);
  }

  @Transactional
  public Achievement syncStationAchievement(Station station, String previousTitle) {
    Achievement achievement = findStationAchievement(station, previousTitle)
        .orElseGet(Achievement::new);

    if (achievement.getUUID() == null) {
      achievement.setUUID(UUID.randomUUID());
    }

    achievement.setTitle(station.getTitle());
    achievement.setDescription("Fullførte " + station.getTitle() + ".");
    achievement.setCategory(station.isBoss() ? AchievementCategory.SPECIAL : AchievementCategory.STOP);
    achievement.setSource(station.getUid().toString());
    achievement.setReward(station.isBoss() ? "Spesialmedalje for å ha slått bossen" : null);

    return achievementRepository.save(achievement);
  }

  private Optional<Achievement> findStationAchievement(Station station, String previousTitle) {
    return achievementRepository.findBySource(station.getUid().toString())
        .or(() -> findLegacyStationAchievement(previousTitle))
        .or(() -> findLegacyStationAchievement(station.getTitle()));
  }

  private Optional<Achievement> findLegacyStationAchievement(String legacyKey) {
    if (legacyKey == null || legacyKey.isBlank()) {
      return Optional.empty();
    }

    return achievementRepository.findBySource(legacyKey)
        .or(() -> achievementRepository.findAllByTitle(legacyKey).stream().findFirst());
  }

  private AchievementResponse unlockAchievementForStudent(StudentUser student, Achievement achievement) {

    boolean alreadyUnlocked = userAchievementRepository.existsByStudentAndAchievement(student, achievement);
    if (alreadyUnlocked) {
      return achievementToResponse(achievement, true);
    }

    UserAchievement userAchievement = new UserAchievement();
    userAchievement.setStudent(student);
    userAchievement.setAchievement(achievement);
    userAchievement.setUnlockedAt(Instant.now());
    userAchievementRepository.save(userAchievement);

    student.addScore(50);
    studentUserRepository.save(student);

    return achievementToResponse(achievement, true);
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
