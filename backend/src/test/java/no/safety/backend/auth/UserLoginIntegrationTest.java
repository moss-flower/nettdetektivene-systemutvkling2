package no.safety.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.leaderboard.repository.ClassroomLeaderboardRepository;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class UserLoginIntegrationTest {
    @MockitoBean
    private ClassroomLeaderboardRepository classroomLeaderboardRepository;

    @Autowired
    private RestTestClient client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.cookie.access.name}")
    private String accessCookieName;

    @Value("${application.security.jwt.cookie.refresh.name}")
    private String refreshCookieName;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@email.no");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.saveAndFlush(user);
    }
    
    @Test
    void shouldLoginUserSuccessfully() {
        UserLoginRequest request = new UserLoginRequest("user@email.no", "password");

        client.post()
            .uri("/api/v1/auth/login")
            .body(request)
            .exchange()
            .expectStatus().isOk() 
            .expectHeader().values(HttpHeaders.SET_COOKIE, setCookies -> {
                assertThat(setCookies).isNotNull();
                assertThat(setCookies).hasSize(2);
                assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains(accessCookieName))).isTrue();
                assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains(refreshCookieName))).isTrue();
                assertThat(setCookies.stream().allMatch(cookie -> cookie.contains("HttpOnly"))).isTrue();
                assertThat(setCookies.stream().allMatch(cookie -> cookie.contains("Secure"))).isTrue();
                assertThat(setCookies.stream().allMatch(cookie -> cookie.contains("SameSite=Strict"))).isTrue();
            })
            .expectBody(UserLoginResponse.class)
            .value(response -> {
                assertThat(response.username()).isEqualTo("user");
            });
    }

    @Test
    void shouldReturn401WhenPasswordIsIncorrect() {
        UserLoginRequest request = new UserLoginRequest("user@email.no", "wrong-password");

        client.post()
            .uri("/api/v1/auth/login")
            .body(request)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().doesNotExist(HttpHeaders.SET_COOKIE)
            .expectBody();
    }

    @Test
    void shouldReturn401WhenUserDoesNotExist() {
        UserLoginRequest request = new UserLoginRequest("unknownUser@email.no", "password");

        client.post()
            .uri("/api/v1/auth/login")
            .body(request)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody();
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() {
        UserLoginRequest request = new UserLoginRequest("invalid email", "");

        client.post()
            .uri("/api/v1/auth/login")
            .body(request)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody();
    }
}
