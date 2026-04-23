package no.safety.backend.notification.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.model.Notification;
import no.safety.backend.notification.repository.NotificationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.service.UserService;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseRegistry registry;
    private final NotificationRepository repository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    /**
     * Captures events linked to Notifications emitted by other files and routes them
     * to the correct recipient.
     * @param event a NotificationEvent type object, include sender ID for mapping to correct targets.
     */
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        logger.debug("Notification event Triggered with event" + event);
        StudentUser sender = (StudentUser) userService.findByPublicUuid(event.getSenderUserUuid()).get();
        Set<User> targets = sender.getClassroom().getOwners();

        for (User user : targets) {
            Notification notification = new Notification();
            
            notification = Notification.builder()
            .message(event.getMessage())
            .target(user)
            .priority(event.getPriority())
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
     * A method for immediately notifying a user of any unread notifications.
     * Called only when a user establishes a connection.
     * Might lead to spam.
     * @param userId internal ID for a given User, not the public facing UUID.
     */
    public void flushPending(Long userId) {
        logger.debug("Flush pending messages called for user id " + userId);
        Set<Notification> notifications = repository.findByTarget_IntenalId(userId);
        if(notifications.isEmpty()) {
            logger.debug("No notifications found for user id " + userId);
            return;
        }
        for (Notification notification : notifications) {
            registry.send(userId, notification);
        }
    }


}
