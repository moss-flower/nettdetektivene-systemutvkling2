package no.safety.backend.classroom.repository;

import java.util.Optional;
import no.safety.backend.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

  List<Classroom> findByOwnerId(Long ownerId);

  Optional<Classroom> findByIdAndOwnerId(Long id, Long ownerId);

  Boolean existsByCode(String code);
}