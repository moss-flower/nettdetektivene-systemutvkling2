package no.safety.backend.achievements.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
public class Achievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, length = 1000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AchievementCategory category;

  @Column(length = 1000)
  private String source;

  @Column(nullable = false)
  private String imageUrl;

  @Column
  private String reward;
}