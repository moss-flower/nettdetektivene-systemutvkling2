package no.safety.backend.notification;

import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.auth.service.JwtService;
import no.safety.backend.notification.controller.NotificationController;
import no.safety.backend.notification.service.NotificationService;
import no.safety.backend.notification.service.SseRegistry;
import no.safety.backend.user.model.User;

@WebMvcTest(NotificationController.class)
@AutoConfigureRestTestClient
public class NotificationControllerTest {

    @Autowired
    RestTestClient client;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private SseRegistry sseRegistry;

    @MockitoBean
    private NotificationService notificationService;

    // private UserPrincipal mockPrincipal;

    @BeforeEach
    void setUp() {
    User mockUser = User.builder()
        .internalId(42L)
        .email("test@test.com")
        .username("testuser")
        .password("password")
        .roles(new HashSet<>())
        .build();

    UserPrincipal principal = UserPrincipal.fromEntity(mockUser);
    
    when(userDetailsService.loadUserByUsername("test@test.com"))
        .thenReturn(principal);
}

    // @Test
    // void shouldEstablishSseConnection() {
    //     when(sseRegistry.register(42L)).thenReturn(new SseEmitter(0L));

    //     client.get().uri("/api/notifications/stream")
    //         .exchange()
    //         .expectStatus().isOk();
    // }
    

    // @Test
    // void shouldReturn401WhenNotAuthenticated() {
        
    //     client.get().uri("/api/notifications/stream")
    //         .exchange()
    //         .expectStatus().isUnauthorized();
    // }
}
