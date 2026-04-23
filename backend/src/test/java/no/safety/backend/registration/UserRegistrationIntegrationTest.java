package no.safety.backend.registration;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import no.safety.backend.BaseIntegrationTest;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

public class UserRegistrationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StudentUserRepository studentUserRepository;

    @Autowired
    private TeacherUserRepository teacherUserRepository;

    @Test
    void shouldRegisterStudentUserSuccessfully() {
        StudentUserRegistrationRequest request = new StudentUserRegistrationRequest("student", "student@email.no", "password");

        client.post()
            .uri("/api/v1/auth/register/student")
            .body(request)
            .exchange()
            .expectStatus().isCreated() 
            .expectBody(StudentUserRegistrationRequest.class);

        assertThat(studentUserRepository.findByUsername("student")).isPresent();
        assertThat(studentUserRepository.findByEmail("student@email.no")).isPresent();
    }

    @Test
    void shouldRegisterTeacherUserSuccessfully() {
        TeacherUserRegistrationRequest request = new TeacherUserRegistrationRequest("teacher", "teacher@email.no", "passord");

        client.post()
            .uri("/api/v1/auth/register/teacher")
            .body(request)
            .exchange()
            .expectStatus().isCreated() 
            .expectBody(TeacherUserRegistrationRequest.class);

        assertThat(teacherUserRepository.findByUsername("teacher")).isPresent();
        assertThat(teacherUserRepository.findByEmail("teacher@email.no")).isPresent();
    }
}
