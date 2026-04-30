package no.safety.backend.notification.dto;

public class WeeklyMysterySubmissionEvent extends NotificationEvent {
    public WeeklyMysterySubmissionEvent(Long senderInternalId, String message) {
        super(senderInternalId, message, 0);
    }
}
