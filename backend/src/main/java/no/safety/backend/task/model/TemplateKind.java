package no.safety.backend.task.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TemplateKind {
  @JsonProperty("x-or-y") X_OR_Y,
  @JsonProperty("multiple-choice") MULTIPLE_CHOICE,
  @JsonProperty("email-hotspots") EMAIL_HOTSPOTS,
  @JsonProperty("sort-into-buckets") SORT_INTO_BUCKETS,
  @JsonProperty("password-strength") PASSWORD_STRENGTH
}
