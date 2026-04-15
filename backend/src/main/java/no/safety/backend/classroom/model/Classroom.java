package no.safety.backend.classroom.model;

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
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "classrooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 40)
  private String code;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 1000)
  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "classroom_owners",
      joinColumns = @JoinColumn(name="classroom_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> owners = new HashSet<User>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Classroom classroom)) return false;
    return id != null && id.equals(classroom.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}