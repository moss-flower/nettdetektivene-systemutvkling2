package no.safety.backend.notification.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.model.Notification;
import no.safety.backend.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseRegistry registry;
    private final NotificationRepository repository;
    
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification;
    }

    //event is trigger in some other class
    //it is transmitted here
    //it is recorded to a database
    //it is pushed (if possible)
}
