package no.safety.backend.station.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.excercise.model.Exercise;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

  //Internal database ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //Public UUID for api calls
  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID publicId;

  @Column(nullable = false)
  private String title;

  @Column(length = 100)
  private String description;

  @Column
  private String backgroundColor;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "station_excercises",
      joinColumns = @JoinColumn(name = "station_id"),
      inverseJoinColumns = @JoinColumn(name = "excercise_id"))
  private Set<Exercise> exercises;

  @PrePersist
  private void onCreate() {
    if (this.publicId == null) {
      this.publicId = UUID.randomUUID();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Station station)) {
      return false;
    }
    return publicId != null && publicId.equals(station.getPublicId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
