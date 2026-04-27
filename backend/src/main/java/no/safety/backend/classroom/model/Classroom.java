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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;

@Entity
@Table(name = "classrooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long internalId;

  @Column(name = "uid", nullable = false, updatable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID uid;

  @Column(nullable = false, unique = true, length = 40)
  private String code;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(name = "world_uid", columnDefinition = "BINARY(16)")
  private UUID worldUid;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "classroom_owners",
      joinColumns = @JoinColumn(name = "classroom_uid", referencedColumnName = "uid"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> owners = new HashSet<>();

  @OneToMany(mappedBy = "classroom")
  private Set<StudentUser> members = new HashSet<>();

  @PrePersist
  private void onCreate() {
    if (uid == null) {
      uid = UUID.randomUUID();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Classroom classroom)) return false;
    return uid != null && uid.equals(classroom.getUid());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
