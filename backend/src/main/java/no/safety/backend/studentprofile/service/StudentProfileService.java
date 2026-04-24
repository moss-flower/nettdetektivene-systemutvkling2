package no.safety.backend.studentprofile.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.studentprofile.dto.StudentProfileResponse;
import no.safety.backend.user.model.StudentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

  private static final int XP_PER_COMPLETED_EXERCISE = 100;
  private static final int XP_PER_UNLOCKED_ACHIEVEMENT = 50;
  private static final int XP_PER_LEVEL = 500;

  private final CompletedExerciseRepository completedExerciseRepository;
  private final UserAchievementRepository userAchievementRepository;

  @Transactional(readOnly = true)
  public StudentProfileResponse getProfile(StudentUser student) {
    UUID studentUid = student.getPublicUuid();
    int completedExercises = (int) completedExerciseRepository.countByStudentUid(studentUid);
    int unlockedAchievements = (int) userAchievementRepository.countByStudent(student);

    int totalXp = (completedExercises * XP_PER_COMPLETED_EXERCISE)
        + (unlockedAchievements * XP_PER_UNLOCKED_ACHIEVEMENT);

    int currentLevel = Math.max(1, (totalXp / XP_PER_LEVEL) + 1);
    int xpAtLevelStart = (currentLevel - 1) * XP_PER_LEVEL;
    int currentXp = Math.max(0, totalXp - xpAtLevelStart);

    return new StudentProfileResponse(
        studentUid,
        student.getUsername(),
        currentLevel,
        currentXp,
        XP_PER_LEVEL,
        completedExercises,
        unlockedAchievements
    );
  }
}
