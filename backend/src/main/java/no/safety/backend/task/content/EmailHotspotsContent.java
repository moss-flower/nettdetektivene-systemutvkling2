package no.safety.backend.task.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

public record EmailHotspotsContent(
    Prompt prompt,
    Email email,
    List<Target> targets
) implements TaskContent {

  public record Prompt(String title, String text) {

  }

  public record Target(String id, boolean isSuspicious) {

  }

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

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = TextBlock.class, name = "text"),
      @JsonSubTypes.Type(value = LinkBlock.class, name = "link"),
  })
  public sealed interface BodyBlock permits TextBlock, LinkBlock {

  }

  public record TextBlock(String id, String text, String targetId) implements BodyBlock {

  }

  public record LinkBlock(
      String id,
      String label,
      String urlText,
      String helperText,
      String targetId
  ) implements BodyBlock {

  }

  public record Attachment(String id, String name, String details, String targetId) {

  }

  public record ActionButton(String label, String helperText, String targetId) {

  }
}
