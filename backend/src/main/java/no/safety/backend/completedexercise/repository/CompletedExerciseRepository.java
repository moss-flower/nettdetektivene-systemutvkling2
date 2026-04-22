package no.safety.backend.completedexercise.repository;

import java.util.Collection;
import java.util.List;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.model.CompletedExerciseId;
import no.safety.backend.user.model.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedExerciseRepository
    extends JpaRepository<CompletedExercise, CompletedExerciseId> {

  boolean existsByStudentAndExerciseId(StudentUser student, String exerciseId);

  List<CompletedExercise> findAllByStudentOrderByExerciseIdAsc(StudentUser student);

  long countByStudentAndExerciseIdIn(StudentUser student, Collection<String> exerciseIds);
}
