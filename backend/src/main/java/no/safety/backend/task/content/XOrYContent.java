package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonProperty;

public record XOrYContent(
    Prompt prompt,
    Options options,
    Choice correctChoice
) implements TaskContent {

  public record Prompt(String title, String text) {

  }

  public record Options(Option x, Option y) {

  }

  public record Option(
      String imageUrl,
      String imageAlt,
      String eyebrow,
      String title,
      String text
  ) {

  }

  public enum Choice {
    @JsonProperty("x") X,
    @JsonProperty("y") Y
  }
}
