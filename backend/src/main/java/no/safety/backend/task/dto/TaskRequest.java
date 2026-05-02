package no.safety.backend.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.safety.backend.task.content.TaskContent;

/**
 * Request DTO for creating or updating a {@link no.safety.backend.task.model.Task}
 *
 * @param introText text shown before the task begins
 * @param feedbackCorrect feedback displayed when the user answers correctly
 * @param feedbackIncorrect feedback displayed when the user answers incorrectly
 * @param content {@link TaskContent} containing the content and type of task
 */
public record TaskRequest(
    @NotBlank(message = "introText is required.")
    @Size(max = 2000, message = "introText cannot exceed 2000 characters.")
    String introText,

    @NotBlank(message = "feedbackCorrect is required.")
    @Size(max = 2000, message = "feedbackCorrect cannot exceed 2000 characters.")
    String feedbackCorrect,

    @NotBlank(message = "feedbackIncorrect is required.")
    @Size(max = 2000, message = "feedbackIncorrect cannot exceed 2000 characters.")
    String feedbackIncorrect,

    @NotNull(message = "content is required.")
    @Valid
    TaskContent content
) {

}
