package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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