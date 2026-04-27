package no.safety.backend.classroom.repository;

import no.safety.backend.classroom.model.ClassroomWeeklyMystery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomWeeklyMysteryRepository extends JpaRepository<ClassroomWeeklyMystery, Long> {
    List<ClassroomWeeklyMystery> findByClassroom_InternalIdOrderByAssignedDateDesc(Long classroomId);
}
