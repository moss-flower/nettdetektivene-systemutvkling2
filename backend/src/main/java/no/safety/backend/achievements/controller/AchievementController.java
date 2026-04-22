package no.safety.backend.achievements.controller;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementModifyRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.user.model.StudentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

  private final AchievementService achievementService;

  /**
   * Returns all achievements, with unlocked = true/false for the current student.
   */
  @GetMapping("/unlocked")
  public List<AchievementResponse> getUnlockedStudentAchievements(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    return achievementService.getAchievementsForStudent((StudentUser) userPrincipal.user());
  }

  /**
   * Returns all achievement definitions.
   */
  @GetMapping("/all")
  public List<AchievementResponse> getAllAchievements() {
    return achievementService.getAllAchievements();
  }

  /**
   * Returns one achievement definition by UUID.
   */
  @GetMapping("/{achievementUUID}")
  public AchievementResponse getAchievementByUUID(@PathVariable UUID achievementUUID) {
    return achievementService.getAchievementByUUID(achievementUUID);
  }

  /**
   * Creates a new achievement definition.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AchievementResponse createAchievement(@RequestBody AchievementModifyRequest request) {
    return achievementService.createAchievement(request);
  }

  /**
   * Updates an existing achievement definition by UUID.
   */
  @PutMapping("/{achievementUUID}")
  public AchievementResponse updateAchievement(
      @PathVariable UUID achievementUUID,
      @RequestBody AchievementModifyRequest request
  ) {
    return achievementService.updateAchievement(achievementUUID, request);
  }

  /**
   * Deletes an achievement definition by UUID.
   */
  @DeleteMapping("/{achievementUUID}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAchievement(@PathVariable UUID achievementUUID) {
    achievementService.deleteAchievement(achievementUUID);
  }
}