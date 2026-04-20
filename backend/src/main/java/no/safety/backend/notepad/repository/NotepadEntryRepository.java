package no.safety.backend.notepad.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.notepad.model.NotepadEntry;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotepadEntryRepository extends JpaRepository<NotepadEntry, Long> {
    boolean existsByPublicUuid(@NonNull UUID publicUuid);

    Optional<NotepadEntry> findByPublicUuid(UUID publicUuid);

    void deleteByPublicUuid(UUID publicUuid);

}
