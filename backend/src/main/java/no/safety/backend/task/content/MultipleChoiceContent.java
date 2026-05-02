package no.safety.backend.task.content;

import java.util.List;

/**
 * {@link TaskContent} for a multiple-choice task where the user picks one
 * answer out of several.
 *
 * @param prompt question shown to the user
 * @param options the list of selectable answers
 * @param correctChoice {@link Option#key()} of the correct option
 */
public record MultipleChoiceContent(
    Prompt prompt,
    List<Option> options,
    String correctChoice
) implements TaskContent {

  /**
   * Question shown above the options.
   *
   * @param imageUrl url of an image for the question
   * @param imageAlt alt text for the image
   * @param title short heading for the question
   * @param text supporting body text
   */
  public record Prompt(
      String imageUrl,
      String imageAlt,
      String title,
      String text
  ) {

  }

  /**
   * A single answer in a {@link MultipleChoiceContent} task.
   *
   * @param key stable id
   * @param title text shown for the option
   */
  public record Option(String key, String title) {

  }
}
