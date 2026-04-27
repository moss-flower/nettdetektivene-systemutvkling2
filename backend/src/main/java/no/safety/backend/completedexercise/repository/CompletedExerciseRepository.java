package no.safety.backend.completedexercise.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.model.CompletedExerciseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedExerciseRepository
    extends JpaRepository<CompletedExercise, CompletedExerciseId> {

  boolean existsByStudentUidAndExerciseUid(UUID studentUid, UUID exerciseUid);

  Optional<CompletedExercise> findByStudentUidAndExerciseUid(UUID studentUid, UUID exerciseUid);

  List<CompletedExercise> findAllByStudentUidOrderByExerciseUidAsc(UUID studentUid);

  long countByStudentUidAndExerciseUidIn(UUID studentUid, Collection<UUID> exerciseUids);

  long countByStudentUid(UUID studentUid);
}
