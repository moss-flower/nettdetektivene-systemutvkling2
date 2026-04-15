package no.safety.backend.classroom.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

  boolean existsByCode(String code);

  boolean existsByIdAndOwners_PublicUuid(Long id, UUID ownerUuid);

  Optional<Classroom> findByIdAndOwners_PublicUuid(Long id, UUID ownerUuid);
}