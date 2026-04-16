package no.safety.backend.registration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.StudentUserRegistrationResponse;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationResponse;
import no.safety.backend.registration.service.UserRegistrationService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationControllerTest {
    
    @Mock
    private UserRegistrationService mockService;

    @InjectMocks
    private UserRegistrationController controller;

    @Test
    void StudentRegistration_successful() {
        // Arrange, Act, Assert (AAA)

        //Arrange
        StudentUserRegistrationRequest request = new StudentUserRegistrationRequest(
            "joe","test@test.com", "secret_password");
        
        StudentUser studentUser = new StudentUser(); 
        studentUser.setEmail(request.email());
        studentUser.setUsername(request.username());
        studentUser.setPassword(request.password());

        Mockito.when(mockService.registerStudent(request)).thenReturn(studentUser);
        StudentUserRegistrationResponse expected = StudentUserRegistrationResponse
        .fromEntity(studentUser, "Student account registered successfully.");

        //Act
        ResponseEntity<StudentUserRegistrationResponse> response
         = controller.RegisterStudentUser(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);  
    }

    // @Test
    // void StudentRegistration_unsuccessful() {
    //     // Arrange, Act, Assert (AAA)

    //     //Arrange
    //     StudentUserRegistrationRequest request = new StudentUserRegistrationRequest(
    //         "joe","test@test.com", "secret_password");
        
    //     StudentUser studentUser = new StudentUser(); 
    //     studentUser.setEmail(request.email());
    //     studentUser.setUsername(request.username());
    //     studentUser.setPassword(request.password());

    //     Mockito.when(mockService.registerStudent(request)).thenThrow(new RuntimeException());

    //     //Act
    //     ResponseEntity<StudentUserRegistrationResponse> response
    //      = controller.RegisterStudentUser(request);

    //     // Assert
    //     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // }

    @Test
    void TeacherRegistration_successful() {
        // Arrange, Act, Assert (AAA)

        //Arrange
        TeacherUserRegistrationRequest request = new TeacherUserRegistrationRequest(
            "joe","test@test.com", "secret_password");
        
        TeacherUser teacherUser = new TeacherUser(); 
        teacherUser.setEmail(request.email());
        teacherUser.setUsername(request.username());
        teacherUser.setPassword(request.password());

        Mockito.when(mockService.registerTeacher(request)).thenReturn(teacherUser);
        TeacherUserRegistrationResponse expected = TeacherUserRegistrationResponse
        .fromEntity(teacherUser, "Student account registered successfully.");

        //Act
        ResponseEntity<TeacherUserRegistrationResponse> response
         = controller.RegisterTeacherUser(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);  
    }
}
