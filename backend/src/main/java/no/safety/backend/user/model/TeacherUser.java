package no.safety.backend.user.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import no.safety.backend.classroom.model.Classroom;

/**
 * Defines a teacher user entity which extends {@link User}
 */
@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherUser extends User {
    @ManyToMany(mappedBy = "owners")
    @Builder.Default
    private Set<Classroom> classrooms = new HashSet<>();
}
