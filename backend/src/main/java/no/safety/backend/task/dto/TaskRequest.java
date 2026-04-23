package no.safety.backend.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.safety.backend.task.content.TaskContent;

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
