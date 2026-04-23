package no.safety.backend.classroom.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import no.safety.backend.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {

  boolean existsByCode(String code);

  boolean existsByUidAndOwners_PublicUuid(UUID uid, UUID ownerUuid);

  Optional<Classroom> findByUidAndOwners_PublicUuid(UUID uid, UUID ownerUuid);

  Set<Classroom> findAllByOwners_PublicUuid(UUID ownerUuid);

  Optional<Classroom> findByCode(String code);

}
