package no.safety.backend.notification.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationEvent {
    private final UUID senderUserUuid;
    private final Long message;
    private final int priority;
}
