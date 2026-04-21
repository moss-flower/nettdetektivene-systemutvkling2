package no.safety.backend.task.content;

import java.util.List;

public record MultipleChoiceContent(
    Prompt prompt,
    List<Option> options,
    String correctChoice
) implements TaskContent {

  public record Prompt(
      String imageUrl,
      String imageAlt,
      String title,
      String text
  ) {

  }

  public record Option(String key, String title) {

  }
}
