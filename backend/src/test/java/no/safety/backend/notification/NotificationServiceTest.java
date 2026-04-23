package no.safety.backend.notification;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.repository.NotificationRepository;
import no.safety.backend.notification.service.NotificationService;
import no.safety.backend.notification.service.SseRegistry;
import no.safety.backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private SseRegistry registry;
    @Mock private NotificationRepository repository;
    @Mock private UserService userService;

    @InjectMocks
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        NotificationEvent mockEvent = new NotificationEvent(new UUID(0, 0), "Something Happened!", 2);
    }
    
}
