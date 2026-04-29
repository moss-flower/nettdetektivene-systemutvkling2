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
import org.springframework.test.web.servlet.client.RestTestClient;

import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class UserLoginIntegrationTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.cookie.name}")
    private String jwtCookieName;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@email.no");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.saveAndFlush(user);
    }
    
    @Test
    void shouldLoginUserSuccessfully() {
        UserLoginRequest request = new UserLoginRequest("user@email.no", "password123");

        client.post()
            .uri("/api/v1/auth/login")
            .body(request)
            .exchange()
            .expectStatus().isOk() 
            .expectHeader().value(HttpHeaders.SET_COOKIE, setCookie -> {
                assertThat(setCookie).isNotNull();
                assertThat(setCookie).contains(jwtCookieName); 
                assertThat(setCookie).contains("HttpOnly");
                assertThat(setCookie).contains("Secure");
                assertThat(setCookie).contains("SameSite=Strict");
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
