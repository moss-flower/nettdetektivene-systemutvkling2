package no.safety.backend.notepad.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.notepad.model.NotepadEntry;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotepadEntryRepository extends JpaRepository<NotepadEntry, Long> {
    boolean existsByPublicUuid(@NonNull UUID publicUuid);

    Optional<NotepadEntry> findByPublicUuid(UUID publicUuid);

    Page<NotepadEntry> findAllByUsers_InternalId(@Param("userId") Long userId, Pageable pageable);

    Optional<NotepadEntry> findByPublicUuidAndUsers_InternalId(UUID publicUuid, Long internalId);

    void deleteByPublicUuid(UUID publicUuid);

}
