package no.safety.backend.notification.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Internal DTO for conveying necessry information to Notification Service.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class NotificationEvent {
    private final UUID senderUserUuid;
    private final String message;
    private final int priority;
}
