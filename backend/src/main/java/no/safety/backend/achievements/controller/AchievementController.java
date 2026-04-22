package no.safety.backend.achievements.controller;

import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.user.model.StudentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

  private final AchievementService achievementService;

  @GetMapping("/unlocked")
  public List<AchievementResponse> getUnlockedStudentAchievements(@AuthenticationPrincipal UserPrincipal userPrincipal) {

    return achievementService.getAchievementsForStudent((StudentUser) userPrincipal.user());
  }

  @GetMapping("/all")
  public List<AchievementResponse> getAllAchievements(Authentication authentication) {

    return achievementService.getAllAchievements();
  }
}