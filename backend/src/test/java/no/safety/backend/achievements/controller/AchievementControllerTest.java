package no.safety.backend.achievements.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import no.safety.backend.achievements.dto.AchievementRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AchievementControllerTest {

  @Mock
  private AchievementService achievementService;

  @InjectMocks
  private AchievementController achievementController;

  @Test
  void getUnlockedStudentAchievements_delegatesToService() {
    StudentUser student = StudentUser.builder().publicUuid(UUID.randomUUID()).build();
    UserPrincipal principal = UserPrincipal.fromEntity(student);
    List<AchievementResponse> expected = List.of(AchievementResponse.builder().title("A").build());

    when(achievementService.getAchievementsForStudent(student)).thenReturn(expected);

    List<AchievementResponse> result = achievementController.getUnlockedStudentAchievements(principal);

    assertSame(expected, result);
    verify(achievementService).getAchievementsForStudent(student);
  }

  @Test
  void getAllAchievements_delegatesToService() {
    List<AchievementResponse> expected = List.of(AchievementResponse.builder().title("All").build());
    when(achievementService.getAllAchievements()).thenReturn(expected);

    List<AchievementResponse> result = achievementController.getAllAchievements();

    assertSame(expected, result);
    verify(achievementService).getAllAchievements();
  }

  @Test
  void getAchievementByUUID_delegatesToService() {
    UUID achievementUid = UUID.randomUUID();
    AchievementResponse expected = AchievementResponse.builder().UUID(achievementUid).title("One").build();
    when(achievementService.getAchievementByUUID(achievementUid)).thenReturn(expected);

    AchievementResponse result = achievementController.getAchievementByUUID(achievementUid);

    assertSame(expected, result);
    verify(achievementService).getAchievementByUUID(achievementUid);
  }

  @Test
  void createAchievement_asTeacher_callsService() {
    AchievementRequest request = new AchievementRequest();
    UserPrincipal teacherPrincipal = principalWithRoles(RoleType.ROLE_TEACHER);
    AchievementResponse expected = AchievementResponse.builder().title("Created").build();
    when(achievementService.createAchievement(request)).thenReturn(expected);

    AchievementResponse result = achievementController.createAchievement(request, teacherPrincipal);

    assertSame(expected, result);
    verify(achievementService).createAchievement(request);
  }

  @Test
  void createAchievement_asStudent_throwsForbidden() {
    AchievementRequest request = new AchievementRequest();
    UserPrincipal studentPrincipal = principalWithRoles(RoleType.ROLE_STUDENT);

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> achievementController.createAchievement(request, studentPrincipal)
    );

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    verifyNoInteractions(achievementService);
  }

  @Test
  void updateAchievement_asAdmin_callsService() {
    UUID achievementUid = UUID.randomUUID();
    AchievementRequest request = new AchievementRequest();
    UserPrincipal adminPrincipal = principalWithRoles(RoleType.ROLE_ADMIN);
    AchievementResponse expected = AchievementResponse.builder().title("Updated").build();
    when(achievementService.updateAchievement(achievementUid, request)).thenReturn(expected);

    AchievementResponse result = achievementController.updateAchievement(
        achievementUid, request, adminPrincipal);

    assertSame(expected, result);
    verify(achievementService).updateAchievement(achievementUid, request);
  }

  @Test
  void deleteAchievement_asStudent_throwsForbidden() {
    UUID achievementUid = UUID.randomUUID();
    UserPrincipal studentPrincipal = principalWithRoles(RoleType.ROLE_STUDENT);

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> achievementController.deleteAchievement(achievementUid, studentPrincipal)
    );

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    verifyNoInteractions(achievementService);
  }

  @Test
  void deleteAchievement_asTeacher_callsService() {
    UUID achievementUid = UUID.randomUUID();
    UserPrincipal teacherPrincipal = principalWithRoles(RoleType.ROLE_TEACHER);

    achievementController.deleteAchievement(achievementUid, teacherPrincipal);

    verify(achievementService).deleteAchievement(achievementUid);
  }

  private static UserPrincipal principalWithRoles(RoleType... roleTypes) {
    Set<Role> roles = java.util.Arrays.stream(roleTypes)
        .map(type -> {
          Role role = new Role();
          role.setType(type);
          return role;
        })
        .collect(java.util.stream.Collectors.toSet());

    User user = User.builder()
        .publicUuid(UUID.randomUUID())
        .username("user")
        .email("user@test.no")
        .password("pw")
        .roles(roles)
        .build();

    return UserPrincipal.fromEntity(user);
  }
}
