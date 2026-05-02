package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link TaskContent} for a binary choice task where the user picks between
 * exactly two alternatives.
 *
 * @param prompt question shown to the user
 * @param options the two choices the user picks between
 * @param correctChoice which of the two options is the correct answer
 */
public record XOrYContent(
    Prompt prompt,
    Options options,
    Choice correctChoice
) implements TaskContent {

  /**
   * Question shown above the options.
   *
   * @param title short heading for the question
   * @param text supporting body text
   */
  public record Prompt(String title, String text) {

  }

  /**
   * The pair of alternatives the user picks between.
   *
   * @param x the {@code X} option
   * @param y the {@code Y} option
   */
  public record Options(Option x, Option y) {

  }

  /**
   * A single alternative in an {@link XOrYContent} task.
   *
   * @param imageUrl url of the option's illustration
   * @param imageAlt alt text for the illustration
   * @param eyebrow short label rendered above the title
   * @param title heading of the option
   * @param text supporting body text
   */
  public record Option(
      String imageUrl,
      String imageAlt,
      String eyebrow,
      String title,
      String text
  ) {

  }

  /**
   * Identifies which of the two {@link Options} is correct.
   */
  public enum Choice {
    @JsonProperty("x") X,
    @JsonProperty("y") Y
  }
}
