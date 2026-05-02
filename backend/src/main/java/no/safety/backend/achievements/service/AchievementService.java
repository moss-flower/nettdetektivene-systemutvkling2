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
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing achievement definitions and user achievement state.
 * Provides CRUD operations for achievements and tracks which students have
 * unlocked each achievement.
 */
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final StudentUserRepository studentUserRepository;

    /**
     * Retrieves all achievements with an indicator of whether the given student has
     * unlocked each one.
     *
     * @param student the student whose unlocked status should be checked.
     * @return a list of achievement responses with unlocked status for the given
     *         student.
     */
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

    /**
     * Retrieves all achievement definitions without unlocked status.
     *
     * @return a list of all achievement responses.
     */
    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(achievement -> achievementToResponse(achievement, false))
                .toList();
    }

    /**
     * Retrieves a single achievement definition by its UUID.
     *
     * @param achievementUUID the UUID of the achievement to retrieve.
     * @return the achievement response for the specified UUID.
     * @throws org.springframework.web.server.ResponseStatusException if the
     *                                                                achievement is
     *                                                                not found.
     */
    @Transactional(readOnly = true)
    public AchievementResponse getAchievementByUUID(UUID achievementUUID) {
        Achievement achievement = achievementRepository.findByUUID(achievementUUID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Achievement not found"));

        return achievementToResponse(achievement, false);
    }

    /**
     * Creates a new achievement definition from the given request.
     *
     * @param request the achievement title, description, category, source, and
     *                reward.
     * @return the created achievement response.
     */
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

    /**
     * Updates an existing achievement definition identified by the given UUID.
     *
     * @param achievementUUID the UUID of the achievement to update.
     * @param request         the new title, description, category, source, and
     *                        reward.
     * @return the updated achievement response.
     * @throws org.springframework.web.server.ResponseStatusException if the
     *                                                                achievement is
     *                                                                not found.
     */
    @Transactional
    public AchievementResponse updateAchievement(UUID achievementUUID, AchievementRequest request) {
        Achievement achievement = achievementRepository.findByUUID(achievementUUID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Achievement not found"));

        achievement.setTitle(request.getTitle());
        achievement.setDescription(request.getDescription());
        achievement.setCategory(request.getCategory());
        achievement.setSource(request.getSource());
        achievement.setReward(request.getReward());

        Achievement saved = achievementRepository.save(achievement);
        return achievementToResponse(saved, false);
    }

    /**
     * Deletes an achievement definition identified by the given UUID.
     *
     * @param achievementUUID the UUID of the achievement to delete.
     * @throws org.springframework.web.server.ResponseStatusException if the
     *                                                                achievement is
     *                                                                not found.
     */
    @Transactional
    public void deleteAchievement(UUID achievementUUID) {
        Achievement achievement = achievementRepository.findByUUID(achievementUUID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Achievement not found"));

        achievementRepository.delete(achievement);
    }

    /**
     * Unlocks an achievement for the given student based on source and category.
     * Awards 50 score points to the student. If the achievement is already
     * unlocked,
     * no duplicate is created.
     *
     * @param student  the student to unlock the achievement for.
     * @param source   the source identifier of the achievement.
     * @param category the category of the achievement.
     * @return the achievement response with unlocked status set to true.
     * @throws no.safety.backend.exception.NotFoundException if no achievement
     *                                                       matches the source and
     *                                                       category.
     */
    @Transactional
    public AchievementResponse unlockAchievementForStudent(StudentUser student, String source,
            AchievementCategory category) {
        Achievement achievement = achievementRepository.findBySourceAndCategory(source, category)
                .orElseThrow(() -> new NotFoundException("Achievement not found for source: " + source));

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

    /**
     * Converts an achievement entity to its response DTO.
     *
     * @param achievement the achievement entity to convert.
     * @param unlocked    whether the achievement is unlocked for the current user.
     * @return the achievement response DTO.
     */
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
