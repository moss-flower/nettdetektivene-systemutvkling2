package no.safety.backend.notification.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NotificationEvent {
    private final UUID senderUserUuid;
    private final String message;
    private final int priority;
}
