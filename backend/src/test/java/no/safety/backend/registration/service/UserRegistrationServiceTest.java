package no.safety.backend.registration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
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

    @InjectMocks
    UserRegistrationService userRegistrationService;

    private StudentUserRegistrationRequest studentUserRegistrationRequest;
    private TeacherUserRegistrationRequest teacherUserRegistrationRequest;

    @BeforeEach
    void setUp() {
        studentUserRegistrationRequest = new StudentUserRegistrationRequest(
            "joe", "test@test.com", "plaintext");
        teacherUserRegistrationRequest = new TeacherUserRegistrationRequest(
            "jane", "jane@test.com", "plaintext");
    }

    @Test
    void registerStudent_successful(){
        //arrange, act assert
        //arrange
        Role studentRole = new Role();
        studentRole.setType(RoleType.ROLE_STUDENT);

        StudentUser savedStudent = StudentUser.builder()
            .username("joe")
            .email("test@test.com")
            .password("encoded")
            .internalId(1l)
            .publicUuid(new UUID(0, 0))
            .roles(Set.of())
            .build();
        
        when(mockRoleRepository.findByType(RoleType.ROLE_STUDENT)).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
        when(mockStudentUserRepository.save(any(StudentUser.class))).thenReturn(savedStudent);

        //act
        StudentUser result = userRegistrationService.registerStudent(studentUserRegistrationRequest);

        //assert
        assertThat(result.getUsername()).isEqualTo("joe");
        verify(passwordEncoder).encode("plaintext");
        verify(mockStudentUserRepository).save(any(StudentUser.class));
    }

    @Test
    void RegisterStudent_shouldThrowWhenStudentRoleNotFound() {
        when(mockRoleRepository.findByType(RoleType.ROLE_STUDENT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRegistrationService.registerStudent(studentUserRegistrationRequest))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Role not found");

        verifyNoInteractions(mockStudentUserRepository);
    }

    @Test
    void registerTeacher_successful(){
        //arrange, act assert
        //arrange
        Role teacherRole = new Role();
        teacherRole.setType(RoleType.ROLE_TEACHER);

        TeacherUser savedTeacher = TeacherUser.builder()
            .username("jane")
            .email("jane@test.com")
            .password("encoded")
            .internalId(1l)
            .publicUuid(new UUID(0, 0))
            .roles(Set.of())
            .build();
        
        when(mockRoleRepository.findByType(RoleType.ROLE_TEACHER)).thenReturn(Optional.of(teacherRole));
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
        when(mockTeacherUserRepository.save(any(TeacherUser.class))).thenReturn(savedTeacher);
        //need error handling in the service file before we can write proper tests

        TeacherUser result = userRegistrationService.registerTeacher(teacherUserRegistrationRequest);

        assertThat(result.getUsername()).isEqualTo("jane");
        verify(passwordEncoder).encode("plaintext");
        verify(mockTeacherUserRepository).save(any(TeacherUser.class));
    }

    @Test
    void RegisterTeacher_shouldThrowWhenRoleNotFound() {
        when(mockRoleRepository.findByType(RoleType.ROLE_TEACHER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRegistrationService.registerTeacher(teacherUserRegistrationRequest))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Role not found");

        verifyNoInteractions(mockTeacherUserRepository);
    }

    @Test
    void RegisterTeacher_shouldThrowWhenUserAlreadyExists() {
        when(mockUserService.existsByEmail(teacherUserRegistrationRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.registerTeacher(teacherUserRegistrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("A user with this email already exists.");

        verifyNoInteractions(mockRoleRepository);
        verifyNoInteractions(mockTeacherUserRepository);
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        Role teacherRole = new Role();
        teacherRole.setType(RoleType.ROLE_TEACHER);

        when(mockRoleRepository.findByType(RoleType.ROLE_TEACHER)).thenReturn(Optional.of(teacherRole));
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
        when(mockTeacherUserRepository.save(any(TeacherUser.class))).thenReturn(any());

        userRegistrationService.registerTeacher(teacherUserRegistrationRequest);

        ArgumentCaptor<TeacherUser> captor = ArgumentCaptor.forClass(TeacherUser.class);
        verify(mockTeacherUserRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(captor.getValue().getPassword()).isNotEqualTo("plaintext");
    }

}
