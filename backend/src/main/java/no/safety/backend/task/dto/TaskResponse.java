package no.safety.backend.task.dto;

import no.safety.backend.task.content.TaskContent;
import no.safety.backend.task.model.Task;
import no.safety.backend.task.model.TemplateKind;

/**
 * A respone DTO for {@link Task}s.
 *
 * @param internalId database id of the task
 * @param templateKind the kind of task template the task uses
 * @param introText text shown before the task begins
 * @param feedbackCorrect feedback displayed when the user answers correctly
 * @param feedbackIncorrect feedback displayed when the user answers incorrectly
 * @param content {@link TaskContent} containing the content and type of task
 */
public record TaskResponse(
    Long internalId,
    TemplateKind templateKind,
    String introText,
    String feedbackCorrect,
    String feedbackIncorrect,
    TaskContent content
) {

  public static TaskResponse from(Task t) {
    return new TaskResponse(
        t.getInternalId(),
        t.getTemplateKind(),
        t.getIntroText(),
        t.getFeedbackCorrect(),
        t.getFeedbackIncorrect(),
        t.getContent()
    );
  }
}
