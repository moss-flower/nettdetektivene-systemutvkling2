package no.safety.backend.classroom.repository;

import no.safety.backend.classroom.model.ClassroomWeeklyMystery;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomWeeklyMysteryRepository extends JpaRepository<ClassroomWeeklyMystery, Long> {
    List<ClassroomWeeklyMystery> findByClassroom_InternalIdOrderByAssignedDateDesc(Long classroomId);
    boolean existsByWeeklyMysteryPrompt_AndClassroom_Owners_InternalId(WeeklyMysteryPrompt prompt, Long teacherInternalId);;
}
