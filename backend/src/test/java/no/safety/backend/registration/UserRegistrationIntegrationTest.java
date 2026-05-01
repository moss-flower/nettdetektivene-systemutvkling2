package no.safety.backend.registration;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class UserRegistrationIntegrationTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private StudentUserRepository studentUserRepository;

    @Autowired
    private TeacherUserRepository teacherUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void ensureRoles() {
        for (RoleType type : RoleType.values()) {
            if (roleRepository.findByType(type).isEmpty()) {
                Role role = new Role();
                role.setType(type);
                roleRepository.save(role);
            }
        }
    }

    @Test
    void shouldRegisterStudentUserSuccessfully() {
        StudentUserRegistrationRequest request = new StudentUserRegistrationRequest("student", "student@email.no", "secret_password");

        client.post()
            .uri("/api/v1/register/student")
            .body(request)
            .exchange()
            .expectStatus().isCreated() 
            .expectBody(StudentUserRegistrationRequest.class);

        assertThat(studentUserRepository.findByUsername("student")).isPresent();
        assertThat(studentUserRepository.findByEmail("student@email.no")).isPresent();
    }

    @Test
    void shouldRegisterTeacherUserSuccessfully() {
        TeacherUserRegistrationRequest request = new TeacherUserRegistrationRequest("teacher", "teacher@email.no", "FtmDqt3A4Qk5WwcgmNtg");

        client.post()
            .uri("/api/v1/register/teacher")
            .body(request)
            .exchange()
            .expectStatus().isCreated() 
            .expectBody(TeacherUserRegistrationRequest.class);

        assertThat(teacherUserRepository.findByUsername("teacher")).isPresent();
        assertThat(teacherUserRepository.findByEmail("teacher@email.no")).isPresent();
    }
}
