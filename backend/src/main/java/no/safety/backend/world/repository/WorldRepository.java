package no.safety.backend.world.repository;

import java.util.Optional;
import java.util.UUID;
import no.safety.backend.world.model.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {

  Optional<World> findByUid(UUID uid);

  @Query("SELECT w FROM World w JOIN w.stationUids s WHERE s = :stationUid")
  Optional<World> findByStationUid(@Param("stationUid") UUID stationUid);
}
