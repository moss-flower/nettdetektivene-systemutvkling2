package no.safety.backend.task.dto;

import no.safety.backend.task.content.TaskContent;
import no.safety.backend.task.model.Task;
import no.safety.backend.task.model.TemplateKind;

public record TaskResponse(
    TemplateKind templateKind,
    String introText,
    String feedbackCorrect,
    String feedbackIncorrect,
    TaskContent content
) {

  public static TaskResponse from(Task t) {
    return new TaskResponse(
        t.getTemplateKind(),
        t.getIntroText(),
        t.getFeedbackCorrect(),
        t.getFeedbackIncorrect(),
        t.getContent()
    );
  }
}
