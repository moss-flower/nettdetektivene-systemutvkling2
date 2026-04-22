package no.safety.backend.notification.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationEvent {
    private final Long targetUserId;
    private final Long message;
    private final int priority;
}
