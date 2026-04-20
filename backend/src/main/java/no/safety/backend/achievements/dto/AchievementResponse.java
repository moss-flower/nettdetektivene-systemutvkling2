package no.safety.backend.achievements.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementResponse {

  private String id;
  private String title;
  private String description;
  private String category;
  private String source;
  private boolean unlocked;
  private String imageUrl;
  private String reward;
}