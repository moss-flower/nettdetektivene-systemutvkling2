package no.safety.backend.achievements.repository;

import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.UserAchievement;
import no.safety.backend.user.model.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

  boolean existsByStudentAndAchievement(StudentUser student, Achievement achievement);

  List<UserAchievement> findByStudent(StudentUser student);
}