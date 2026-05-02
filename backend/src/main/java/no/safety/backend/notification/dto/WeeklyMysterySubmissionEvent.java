package no.safety.backend.notification.dto;

/**
 * Event triggered when a student submits a weekly mystery.
 * Has a default priority of 0 (lowest priority).
 */
public class WeeklyMysterySubmissionEvent extends NotificationEvent {
    public WeeklyMysterySubmissionEvent(Long senderInternalId, String message) {
        super(senderInternalId, message, 0);
    }
}
