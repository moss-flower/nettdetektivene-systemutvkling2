package no.safety.backend.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import no.safety.backend.notification.model.Notification;

/**
 * DTO for notification responses sent via SSE.
 * Contains the notification details to be sent to the client.
 */
public record NotificationDto(
        UUID notificationUuid,
        String senderName,
        String message,
        LocalDateTime timestamp,
        int priority,
        UUID senderUuid) {
    /**
     * Converts a {@link Notification} entity to a DTO.
     *
     * @param notification the notification entity to convert
     * @return the corresponding notification DTO
     */
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
                notification.getPublicId(),
                notification.getSenderName(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getPriority(),
                notification.getSenderId());
    }
}
