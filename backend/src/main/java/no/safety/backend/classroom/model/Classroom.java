package no.safety.backend.classroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "classrooms")
public class Classroom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long classroomId;

  @Column(nullable = false, unique = true)
  private String joinCode;

  @Column(nullable = false)
  private String name;

  private String description;

  @ManyToMany
  private Set<TeacherUser> owner;
}