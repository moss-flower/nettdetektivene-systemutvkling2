package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * {@link TaskContent} for a task where the user has to produce a password that
 * reaches a minimum strength score, either by composing one from scratch or by
 * improving a starting password.
 *
 * @param prompt question shown to the user
 * @param mode whether the user composes a new password or improves a given one
 * @param placeholder placeholder text shown in the password input
 * @param startingPassword pre-filled password used when {@link Mode#IMPROVE} is selected
 * @param minLength minimum number of characters the password must contain, or {@code null} for no minimum
 * @param minScoreToPass strength score the password must reach to pass the task
 * @param commonPatterns patterns that should be flagged as weak when present in the password
 */
public record PasswordStrengthContent(
    Prompt prompt,
    Mode mode,
    String placeholder,
    String startingPassword,
    Integer minLength,
    int minScoreToPass,
    List<String> commonPatterns
) implements TaskContent {

  /**
   * Question shown above the password input.
   *
   * @param title short heading for the question
   * @param text supporting body text
   * @param helperText additional guidance shown alongside the input
   */
  public record Prompt(String title, String text, String helperText) {

  }

  /**
   * Whether the user is composing a new password or improving an existing one.
   */
  public enum Mode {
    @JsonProperty("create") CREATE,
    @JsonProperty("improve") IMPROVE
  }
}
