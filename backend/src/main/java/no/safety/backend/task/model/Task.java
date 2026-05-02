package no.safety.backend.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.task.content.EmailHotspotsContent;
import no.safety.backend.task.content.MultipleChoiceContent;
import no.safety.backend.task.content.PasswordStrengthContent;
import no.safety.backend.task.content.SortIntoBucketsContent;
import no.safety.backend.task.content.TaskContent;
import no.safety.backend.task.content.XOrYContent;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Defines a task entity.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long internalId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @Enumerated(EnumType.STRING)
  @Column(name = "template_kind", nullable = false, length = 32)
  private TemplateKind templateKind;

  @Column(nullable = false, length = 2000)
  private String introText;

  @Column(nullable = false, length = 2000)
  private String feedbackCorrect;

  @Column(nullable = false, length = 2000)
  private String feedbackIncorrect;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "json")
  private TaskContent content;

  @PrePersist
  @PreUpdate
  private void syncTemplateKind() {
    if (content == null) {
      return;
    }
    TemplateKind derived = switch (content) {
      case XOrYContent ignored -> TemplateKind.X_OR_Y;
      case MultipleChoiceContent ignored -> TemplateKind.MULTIPLE_CHOICE;
      case EmailHotspotsContent ignored -> TemplateKind.EMAIL_HOTSPOTS;
      case SortIntoBucketsContent ignored -> TemplateKind.SORT_INTO_BUCKETS;
      case PasswordStrengthContent ignored -> TemplateKind.PASSWORD_STRENGTH;
    };
    if (templateKind != null && templateKind != derived) {
      throw new IllegalStateException(
          "templateKind " + templateKind + " does not match content " + content.getClass()
              .getSimpleName());
    }
    this.templateKind = derived;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Task task)) {
      return false;
    }
    return internalId != null && internalId.equals(task.getInternalId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
