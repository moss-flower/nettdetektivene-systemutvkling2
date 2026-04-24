package no.safety.backend.world.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "worlds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class World {

  @Id
  @Column(name = "uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
  private UUID uid;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String image;

  @ElementCollection
  @CollectionTable(name = "world_stations", joinColumns = @JoinColumn(name = "world_uid"))
  @Column(name = "station_uid", columnDefinition = "BINARY(16)")
  @OrderColumn(name = "sort_order")
  private List<UUID> stationUids = new ArrayList<>();

  @PrePersist
  private void onCreate() {
    if (uid == null) {
      uid = UUID.randomUUID();
    }
  }
}
