package no.safety.backend.registration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.StudentUserRegistrationResponse;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationResponse;
import no.safety.backend.registration.service.UserRegistrationService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.mockito.Mockito.verifyNoInteractions;


@WebMvcTest(UserRegistrationController.class)
@AutoConfigureRestTestClient
public class UserRegistrationControllerTest {

    @Autowired
    RestTestClient client;
    
    @MockitoBean
    private UserRegistrationService mockService;

    private StudentUserRegistrationRequest validRequest;
    private StudentUser savedStudent;
    private TeacherUserRegistrationRequest validTeacherRequest;
    private TeacherUser savedTeacher;

    @BeforeEach
    void setUp() {
        validRequest = new StudentUserRegistrationRequest(
            "joe","test@test.com", "secret_password");
        savedStudent = StudentUser.builder()
            .username("joe")
            .email("test@test.com")
            .password("supersecretpass")
            .internalId(1l)
            .publicUuid(new UUID(0, 0))
            .roles(Set.of())
            .build();
        
        validTeacherRequest = new TeacherUserRegistrationRequest(
            "jane", "jane@test.com", "sup");
        savedTeacher = TeacherUser.builder()
            .username("jane")
            .email("jane@test.com")
            .build();
        
    }

    @Test
    @WithMockUser
    void StudentRegistration_successful() {
        // Arrange, Act, Assert (AAA)
        //Arrange
        when(mockService.registerStudent(any(StudentUserRegistrationRequest.class)))
            .thenReturn(savedStudent);

        client.post().uri("/api/v1/register/student")
            .contentType(MediaType.APPLICATION_JSON)
            .body(validRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(StudentUserRegistrationResponse.class)
            .value(response -> {
                assertThat(response.username()).isEqualTo("joe");
                assertThat(response.message()).isEqualTo("Student account registered successfully.");
            });
    }

    @Test
    void StudentRegistration_shouldReturn400OnInvalidRequest() {
        StudentUserRegistrationRequest invalidRequest =
                new StudentUserRegistrationRequest("", "", "");

        client.post().uri("/api/v1/register/student")
            .contentType(MediaType.APPLICATION_JSON)
            .body(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();

        verifyNoInteractions(mockService);
    }

    @Test
    void TeacherRegistration_successful() {
        // Arrange, Act, Assert (AAA)


    }
}
