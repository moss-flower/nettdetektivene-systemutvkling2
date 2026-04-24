package no.safety.backend.station.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long internalId;

  @Column(name = "uid", nullable = false, updatable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID uid;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String image;

  @Column(name = "background_color", length = 20)
  private String backgroundColor;

  @ElementCollection
  @CollectionTable(
      name = "station_exercises",
      joinColumns = @JoinColumn(name = "station_uid", referencedColumnName = "uid")
  )
  @Column(name = "exercise_uid", columnDefinition = "BINARY(16)")
  @OrderColumn(name = "sort_order")
  private List<UUID> exerciseUids = new ArrayList<>();

  @PrePersist
  private void onCreate() {
    if (uid == null) {
      uid = UUID.randomUUID();
    }
  }
}
