package no.safety.backend.completedexercise.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.model.CompletedExerciseId;
import no.safety.backend.user.model.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedExerciseRepository
    extends JpaRepository<CompletedExercise, CompletedExerciseId> {

  boolean existsByStudentAndExerciseUid(StudentUser student, UUID exerciseUid);

  List<CompletedExercise> findAllByStudentOrderByExerciseUidAsc(StudentUser student);

  long countByStudentAndExerciseUidIn(StudentUser student, Collection<UUID> exerciseUids);
}
