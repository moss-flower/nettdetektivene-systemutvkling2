package no.safety.backend.studentprofile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.studentprofile.dto.StudentProfileResponse;
import no.safety.backend.user.model.StudentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentProfileServiceTest {

  @Mock
  private CompletedExerciseRepository completedExerciseRepository;

  @Mock
  private UserAchievementRepository userAchievementRepository;

  @InjectMocks
  private StudentProfileService studentProfileService;

  @Test
  void getProfile_returnsExpectedSummary() {
    UUID studentUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder()
        .publicUuid(studentUid)
        .username("alice")
        .build();

    when(completedExerciseRepository.countByStudentUid(studentUid)).thenReturn(3L);
    when(userAchievementRepository.countByStudent(student)).thenReturn(2L);

    StudentProfileResponse response = studentProfileService.getProfile(student);

    assertEquals(studentUid, response.studentUid());
    assertEquals("alice", response.studentName());
    assertEquals(400, response.totalXp());
    assertEquals(3, response.completedExercises());
    assertEquals(2, response.unlockedAchievements());

    verify(completedExerciseRepository).countByStudentUid(studentUid);
    verify(userAchievementRepository).countByStudent(student);
  }
}
