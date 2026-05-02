package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 * {@link TaskContent} for a task where the user inspects an email and
 * marks the parts that look suspicious. Each clickable region of the email
 * carries a {@code targetId} that points back to a {@link Target} describing
 * whether that region should be flagged.
 *
 * @param prompt question shown to the user
 * @param email the email body and metadata to inspect
 * @param targets all clickable regions and whether each is suspicious
 */
public record EmailHotspotsContent(
    Prompt prompt,
    Email email,
    List<Target> targets
) implements TaskContent {

  /**
   * Question shown above the email.
   *
   * @param title short heading for the question
   * @param text supporting body text
   */
  public record Prompt(String title, String text) {

  }

  /**
   * A clickable region of the email referenced by {@code targetId} on the
   * matching {@link Email} field or {@link BodyBlock}.
   *
   * @param id stable id
   * @param isSuspicious {@code true} if the region should be flagged as a hotspot
   */
  public record Target(String id, boolean isSuspicious) {

  }

  /**
   * The rendered email under inspection. Each {@code …TargetId} field links the
   * adjacent piece of content to a {@link Target} so the UI knows whether
   * clicking it counts as identifying a suspicious element.
   *
   * @param senderName display name of the sender
   * @param senderNameTargetId target id linked to {@code senderName}
   * @param senderAddress email address of the sender
   * @param senderAddressTargetId target id linked to {@code senderAddress}
   * @param subject subject line of the email
   * @param subjectTargetId target id linked to {@code subject}
   * @param senderAvatarText short text shown inside the sender avatar
   * @param receivedLabel label describing when the email was received
   * @param receivedLabelTargetId target id linked to {@code receivedLabel}
   * @param greeting opening greeting line
   * @param greetingTargetId target id linked to {@code greeting}
   * @param bodyBlocks ordered blocks making up the email body
   * @param actionButton call-to-action rendered with the email
   * @param attachments files attached to the email
   * @param closing closing line of the email
   * @param closingTargetId target id linked to {@code closing}
   */
  public record Email(
      String senderName,
      String senderNameTargetId,
      String senderAddress,
      String senderAddressTargetId,
      String subject,
      String subjectTargetId,
      String senderAvatarText,
      String receivedLabel,
      String receivedLabelTargetId,
      String greeting,
      String greetingTargetId,
      List<BodyBlock> bodyBlocks,
      ActionButton actionButton,
      List<Attachment> attachments,
      String closing,
      String closingTargetId
  ) {

  }

  /**
   * Sealed type for a single block in the email body. The {@code type} JSON
   * property selects between {@link TextBlock} and {@link LinkBlock}.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = TextBlock.class, name = "text"),
      @JsonSubTypes.Type(value = LinkBlock.class, name = "link"),
  })
  public sealed interface BodyBlock permits TextBlock, LinkBlock {

  }

  /**
   * Plain-text block in the email body.
   *
   * @param id stable id
   * @param text the text to render
   * @param targetId target id linked to this block
   */
  public record TextBlock(String id, String text, String targetId) implements BodyBlock {

  }

  /**
   * Link block in the email body, rendered as a clickable link with optional
   * helper text.
   *
   * @param id stable id
   * @param label visible label of the link
   * @param urlText url text shown to the user
   * @param helperText supporting text rendered alongside the link
   * @param targetId target id linked to this block
   */
  public record LinkBlock(
      String id,
      String label,
      String urlText,
      String helperText,
      String targetId
  ) implements BodyBlock {

  }

  /**
   * A file attached to the {@link Email}.
   *
   * @param id stable id
   * @param name display name of the attachment
   * @param details additional details such as file size or type
   * @param targetId target id linked to this attachment
   */
  public record Attachment(String id, String name, String details, String targetId) {

  }

  /**
   * Call-to-action button rendered with the {@link Email}.
   *
   * @param label visible label of the button
   * @param helperText supporting text rendered next to the button
   * @param targetId target id linked to this button
   */
  public record ActionButton(String label, String helperText, String targetId) {

  }
}
