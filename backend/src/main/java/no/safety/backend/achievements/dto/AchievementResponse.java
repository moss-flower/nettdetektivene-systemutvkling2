package no.safety.backend.achievements.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementResponse {
  private UUID UUID;
  private String title;
  private String description;
  private String category;
  private String source;
  private boolean unlocked;
  private String imageUrl;
  private String reward;
}