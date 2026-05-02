package no.safety.backend.notification.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Base event class for notification-related events.
 * Contains sender ID, message, and priority for creating notifications.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class NotificationEvent {
    private final Long senderInternalId;
    private final String message;
    private final int priority;
}
