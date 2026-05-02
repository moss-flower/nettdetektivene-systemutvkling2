package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Sealed marker interface for the payload of a task. Each permitted variant
 * corresponds to a {@link no.safety.backend.task.model.TemplateKind}, and the
 * {@code templateKind} JSON property selects the type when
 * serialising or deserialising.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "templateKind")
@JsonSubTypes({
    @JsonSubTypes.Type(value = XOrYContent.class, name = "x-or-y"),
    @JsonSubTypes.Type(value = MultipleChoiceContent.class, name = "multiple-choice"),
    @JsonSubTypes.Type(value = EmailHotspotsContent.class, name = "email-hotspots"),
    @JsonSubTypes.Type(value = SortIntoBucketsContent.class, name = "sort-into-buckets"),
    @JsonSubTypes.Type(value = PasswordStrengthContent.class, name = "password-strength"),
})
public sealed interface TaskContent
    permits XOrYContent, MultipleChoiceContent, EmailHotspotsContent,
    SortIntoBucketsContent, PasswordStrengthContent {

}