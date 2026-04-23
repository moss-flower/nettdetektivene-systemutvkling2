package no.safety.backend.world.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.station.model.Station;

@Entity
@Table(name = "worlds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class World {

  //Internal database ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //Public UID for api calls
  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private String publicId;

  @Column(nullable = false)
  private String image;

  @Column
  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "world_stations",
      joinColumns = @JoinColumn(name = "world_id"),
      inverseJoinColumns = @JoinColumn(name = "station_id"))
  private List<Station> stations;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof World world)) {
      return false;
    }
    return publicId != null && publicId.equals(world.getPublicId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
