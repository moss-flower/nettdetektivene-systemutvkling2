package no.safety.backend.station.repository;

import java.util.UUID;
import java.util.Optional;
import no.safety.backend.station.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

  Optional<Station> findByUid(UUID uid);

  @Query("SELECT s FROM Station s JOIN s.exerciseUids e WHERE e = :exerciseUid")
  Optional<Station> findByExerciseUid(@Param("exerciseUid") UUID exerciseUid);
}
