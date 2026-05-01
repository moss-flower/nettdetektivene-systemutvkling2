package no.safety.backend.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import no.safety.backend.notification.model.Notification;

/**
 * Internal DTO for conveying necessry information to Notification Service.
 */
public record NotificationDto(
    UUID notificationUuid,
    String senderName,
    String message,
    LocalDateTime timestamp,
    int priority,
    UUID senderUuid
) {
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
            notification.getPublicId(),
            notification.getSenderName(),
            notification.getMessage(),
            notification.getCreatedAt(),
            notification.getPriority(),
            notification.getSenderId()
        );
    }
}
