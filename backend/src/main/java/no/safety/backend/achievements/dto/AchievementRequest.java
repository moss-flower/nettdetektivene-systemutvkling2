package no.safety.backend.achievements.dto;

import lombok.Getter;
import lombok.Setter;
import no.safety.backend.achievements.model.AchievementCategory;

@Getter
@Setter
public class AchievementRequest {
  private String title;
  private String description;
  private AchievementCategory category;
  private String source;
  private String reward;
}
