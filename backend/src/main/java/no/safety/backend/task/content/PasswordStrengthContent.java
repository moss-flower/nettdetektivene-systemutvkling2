package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PasswordStrengthContent(
    Prompt prompt,
    Mode mode,
    String placeholder,
    String startingPassword,
    Integer minLength,
    int minScoreToPass,
    List<String> commonPatterns
) implements TaskContent {

  public record Prompt(String title, String text, String helperText) {

  }

  public enum Mode {
    @JsonProperty("create") CREATE,
    @JsonProperty("improve") IMPROVE
  }
}
