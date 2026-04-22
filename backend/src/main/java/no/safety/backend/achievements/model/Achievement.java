package no.safety.backend.achievements.model;

import java.util.UUID;

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
  private Long internalId;

  @Column(nullable = false, unique = true)
  private UUID UUID;

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