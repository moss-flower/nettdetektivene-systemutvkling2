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
import no.safety.backend.registration.service.UserRegistrationService;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;

    @PostMapping(value = "/student")
    public ResponseEntity<String> RegisterStudentUser(@Valid @RequestBody StudentUserRegistrationRequest request){
        userRegistrationService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student account registered.");
    }
}
