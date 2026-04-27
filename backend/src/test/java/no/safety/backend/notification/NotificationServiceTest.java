package no.safety.backend.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.model.Notification;
import no.safety.backend.notification.repository.NotificationRepository;
import no.safety.backend.notification.service.NotificationService;
import no.safety.backend.notification.service.SseRegistry;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private SseRegistry registry;
    @Mock private NotificationRepository repository;
    @Mock private UserService userService;

    @InjectMocks
    NotificationService notificationService;

    StudentUser sender;
    NotificationEvent mockEvent;
    User target;
    Set<User> targets;

    @BeforeEach
    void setUp() {
        mockEvent = new NotificationEvent(new UUID(0, 0), "Something Happened!", 2);
        target = User.builder()
            .publicUuid(new UUID(1, 0))
            .internalId((2l))
            .email("teacher@test.com")
            .username("jane")
            .password("password456")
            .build();
        targets = new HashSet<>();
        targets.add(target);

        Classroom classroom = new Classroom(
            0L,
            UUID.randomUUID(),
            "chicken",
            "cicada",
            "desc",
            UUID.randomUUID(),
            targets,
            Set.of()
        );
        sender = StudentUser.builder()
            .publicUuid(new UUID(0, 0))
            .internalId(0l)
            .email("test@test.com")
            .username("joe")
            .password("password123")
            .build();
        
        sender.setClassroom(classroom);
    }

    @Test
    void shouldSaveNotificationOnEvent(){
        //arrange, act, assert
        when(userService.findByPublicUuid(any())).thenReturn(Optional.of(sender));

        notificationService.handleNotificationEvent(mockEvent);

        verify(repository).save(any(Notification.class));
    }

    @Test
    void shouldPushToRegistryAfterSaving() {
        when(userService.findByPublicUuid(any())).thenReturn(Optional.of(sender));
        notificationService.handleNotificationEvent(mockEvent);
        verify(registry).send(eq(2L), any());
    }

    @Test
    void shouldStillSaveIfUserOffline() {
        when(userService.findByPublicUuid(any())).thenReturn(Optional.of(sender));
        
        doThrow(new RuntimeException("No emitter"))
        .when(registry).send(eq(2l), any());

        notificationService.handleNotificationEvent(mockEvent);

        verify(repository).save(any(Notification.class));
    }
    
}
