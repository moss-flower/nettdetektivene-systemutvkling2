package no.safety.backend.classroom.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import no.safety.backend.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

  boolean existsByCode(String code);

  boolean existsByIdAndOwners_PublicUuid(Long id, UUID ownerUuid);

  Optional<Classroom> findByIdAndOwners_PublicUuid(Long id, UUID ownerUuid);

  Set<Classroom> findAllByOwners_PublicUuid(UUID ownerUuid);

  Optional<Classroom> findByCode(String code);

  boolean existsByMembers_PublicUuid(UUID memberUuid);
}