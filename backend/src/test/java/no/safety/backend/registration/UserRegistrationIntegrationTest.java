package no.safety.backend.registration;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class UserRegistrationIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");
    
    @Autowired
    private RestTestClient client;

    @Autowired
    private StudentUserRepository studentUserRepository;

    @Autowired
    private TeacherUserRepository teacherUserRepository;

    @Test
    void shouldRegisterStudentUserSuccessfully() {
        StudentUserRegistrationRequest request = new StudentUserRegistrationRequest("bruker", "bruker@email.no", "passord");

        client.post()
            .uri("/api/v1/register/student")
            .body(request)
            .exchange()
            .expectStatus().isCreated() 
            .expectBody(String.class);

        assertThat(studentUserRepository.findByUsername("bruker")).isPresent();
        assertThat(studentUserRepository.findByEmail("bruker@email.no")).isPresent();
    }
}
