package no.safety.backend.notification.service;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        logger.debug("Notification event Triggered");
        StudentUser sender = (StudentUser) userService.findByPublicUuid(event.getSenderUserUuid()).get();
        Set<User> targets = sender.getClassroom().getOwners();

        for (User user : targets) {
            Notification notification = new Notification();
            
            notification = Notification.builder()
            .message(event.getMessage().toString())
            .target(user)
            .priority(event.getPriority())
            .build();

            repository.save(notification);
            registry.send(user.getInternalId(), notification);
        }
    }

    public void flushPending(Long userId) {
        Set<Notification> notifications = repository.findByTarget_IntenalId(userId);
        if(notifications.isEmpty()) {
            return;
        }
        for (Notification notification : notifications) {
            registry.send(userId, notification);
        }
    }


}
