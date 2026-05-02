package no.safety.backend.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.StudentUserRegistrationResponse;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationResponse;
import no.safety.backend.registration.service.UserRegistrationService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;

/**
 * REST controller for user registration endpoints.
 * Provides endpoints for registering new student and teacher accounts.
 */
@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;

    /**
     * Registers a new student user account.
     *
     * @param request the student registration request containing username, email, and password
     * @return a response containing the created student user details and success message
     */
    @PostMapping(value = "/student")
    public ResponseEntity<StudentUserRegistrationResponse> RegisterStudentUser(@Valid @RequestBody StudentUserRegistrationRequest request){
        StudentUser savedUser = userRegistrationService.registerStudent(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StudentUserRegistrationResponse.fromEntity(
                        savedUser, 
                        "Student account registered successfully.")
                );
    }

    /**
     * Registers a new teacher user account.
     *
     * @param request the teacher registration request containing username, email, and password
     * @return a response containing the created teacher user details and success message
     */
    @PostMapping(value = "/teacher")
    public ResponseEntity<TeacherUserRegistrationResponse> RegisterTeacherUser(@Valid @RequestBody TeacherUserRegistrationRequest request){
        TeacherUser savedUser = userRegistrationService.registerTeacher(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TeacherUserRegistrationResponse.fromEntity(
                        savedUser, 
                        "Teacher account registered successfully.")
                );
    }
}
