package no.safety.backend.notification.service;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.AccessDeniedException;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.model.Notification;
import no.safety.backend.notification.repository.NotificationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.service.UserService;

/**
 * Service for managing notifications.
 * Handles notification events, delivers notifications via SSE, and manages notification persistence.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseRegistry registry;
    private final NotificationRepository repository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    /**
     * Handles notification events emitted by other parts of the application.
     * Creates and sends notifications to the appropriate teachers based on the event sender's classroom.
     *
     * @param event the notification event containing message, sender ID, and priority
     */
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        logger.debug("Notification event Triggered with event" + event);
        StudentUser sender = userService.findStudentByInternalId(event.getSenderInternalId()).get();
        Set<TeacherUser> targets = sender.getClassroom().getOwners();

        for (User user : targets) {
            Notification notification = new Notification();
            
            notification = Notification.builder()
            .message(event.getMessage())
            .target(user)
            .senderId(sender.getPublicUuid())
            .priority(event.getPriority())
            .senderName(sender.getUsername())
            .build();

            repository.save(notification);
            try {
                registry.send(user.getInternalId(), notification);
            } catch (Exception e) {
                logger.debug("Error: Failed to send");
            }
        }
    }

    /**
     * Sends all pending notifications to a user who has just established an SSE connection.
     *
     * @param userId the internal ID of the user to flush notifications for
     */
    public void flushPending(Long userId) {
        logger.debug("Flush pending messages called for user id " + userId);
        Set<Notification> notifications = repository.findByTarget_InternalId(userId);
        if(notifications.isEmpty()) {
            logger.debug("No notifications found for user id " + userId);
            return;
        }
        for (Notification notification : notifications) {
            registry.send(userId, notification);
        }
    }

    /**
     * Deletes a notification if the requesting user is the target of the notification.
     *
     * @param notificationUuid the UUID of the notification to delete
     * @param requestingUserId the internal ID of the user requesting the deletion
     */
    public void deleteNotification(UUID notificationUuid, Long requestingUserId) {
        Notification notification = repository.findByPublicId(notificationUuid)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getTarget().getInternalId().equals(requestingUserId)) {
            throw new AccessDeniedException("Not authorized to delete this notification");
        }

        repository.delete(notification);
    }


}
