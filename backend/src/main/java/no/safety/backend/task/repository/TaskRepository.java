package no.safety.backend.task.repository;

import no.safety.backend.task.model.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

  @Modifying
  @Query("delete from Task t where t.exercise.internalId = :exerciseId")
  void deleteByExerciseId(@Param("exerciseId") Long exerciseId);
}
