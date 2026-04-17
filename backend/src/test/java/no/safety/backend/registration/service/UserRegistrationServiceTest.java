package no.safety.backend.registration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.service.UserService;


@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTest {

    MockMvc mock;
    
    @Mock private UserService mockUserService;
    @Mock private StudentUserRepository mockStudentUserRepository;
    @Mock private TeacherUserRepository mockTeacherUserRepository;
    @Mock private RoleRepository mockRoleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void registerStudent_successful(){
        //arrange, act assert
        //arrange

        StudentUserRegistrationRequest request = new StudentUserRegistrationRequest(
            "harry", "harry@potter.com", "dumbledore");

        //need error handling in the service file before we can write proper tests

    }

}
