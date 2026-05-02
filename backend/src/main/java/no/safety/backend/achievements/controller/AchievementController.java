package no.safety.backend.achievements.controller;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.user.model.StudentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Achievement controller that defines API endpoints for managing achievement
 * definitions and viewing unlocked achievements. Read-only endpoints are open
 * to authenticated users, while mutating operations require the caller to be a
 * teacher or admin.
 */
@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    /**
     * Returns all achievements with an indicator of whether the current student has
     * unlocked each one.
     *
     * @param userPrincipal Spring Security principal for the calling user.
     * @return a list of achievement responses with unlocked status for the calling
     *         student.
     * @throws org.springframework.web.server.ResponseStatusException if the caller
     * is not a student.
     */
    @GetMapping("/unlocked")
    public List<AchievementResponse> getUnlockedStudentAchievements(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return achievementService.getAchievementsForStudent((StudentUser) userPrincipal.user());
    }

    /**
     * Returns all achievement definitions without unlocked status.
     *
     * @return a list of all achievement responses.
     */
    @GetMapping("/all")
    public List<AchievementResponse> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    /**
     * Returns a single achievement definition by its UUID.
     *
     * @param achievementUUID the UUID of the achievement to retrieve.
     * @return the achievement response for the specified UUID.
     * @throws org.springframework.web.server.ResponseStatusException if the
     * achievement is not found.
     */
    @GetMapping("/{achievementUUID}")
    public AchievementResponse getAchievementByUUID(@PathVariable UUID achievementUUID) {
        return achievementService.getAchievementByUUID(achievementUUID);
    }

    /**
     * Creates a new achievement definition.
     *
     * @param request       the achievement title, description, category, source,
     *                      and reward.
     * @param userPrincipal Spring Security principal for the calling user.
     * @return the created achievement response.
     * @throws org.springframework.web.server.ResponseStatusException if the caller
     * is not a teacher or admin.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponse createAchievement(
            @RequestBody AchievementRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boolean isAllowed = userPrincipal.user().getRoles().stream()
                .anyMatch(role -> role.getType() == RoleType.ROLE_TEACHER ||
                        role.getType() == RoleType.ROLE_ADMIN);

        if (!isAllowed) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers and admins can create achievements");
        }

        return achievementService.createAchievement(request);
    }

    /**
     * Updates an existing achievement definition by its UUID.
     *
     * @param achievementUUID the UUID of the achievement to update.
     * @param request         the new title, description, category, source, and
     *                        reward.
     * @param userPrincipal   Spring Security principal for the calling user.
     * @return the updated achievement response.
     * @throws org.springframework.web.server.ResponseStatusException if the
     * achievement is not found, or if the caller is not a teacher or admin.
     */
    @PutMapping("/{achievementUUID}")
    public AchievementResponse updateAchievement(
            @PathVariable UUID achievementUUID,
            @RequestBody AchievementRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boolean isAllowed = userPrincipal.user().getRoles().stream()
                .anyMatch(role -> role.getType() == RoleType.ROLE_TEACHER ||
                        role.getType() == RoleType.ROLE_ADMIN);

        if (!isAllowed) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers and admins can update achievements");
        }

        return achievementService.updateAchievement(achievementUUID, request);
    }

    /**
     * Deletes an achievement definition by its UUID.
     *
     * @param achievementUUID the UUID of the achievement to delete.
     * @param userPrincipal   Spring Security principal for the calling user.
     * @throws org.springframework.web.server.ResponseStatusException if the
     * achievement is not found, or if the caller is not a teacher or admin.
     */
    @DeleteMapping("/{achievementUUID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAchievement(
            @PathVariable UUID achievementUUID,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boolean isAllowed = userPrincipal.user().getRoles().stream()
                .anyMatch(role -> role.getType() == RoleType.ROLE_TEACHER ||
                        role.getType() == RoleType.ROLE_ADMIN);

        if (!isAllowed) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers and admins can delete achievements");
        }

        achievementService.deleteAchievement(achievementUUID);
    }
}