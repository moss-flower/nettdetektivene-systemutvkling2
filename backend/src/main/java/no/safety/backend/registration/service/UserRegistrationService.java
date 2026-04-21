package no.safety.backend.registration.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.service.UserService;

/**
 * A service that handles registration of different user accounts e.g. student accounts and teacher accounts
 */
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final StudentUserRepository studentUserRepository;
    private final TeacherUserRepository teacherUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new student user in the database and assigns the default student role.
     *
     * @param request the {@link StudentUserRegistrationRequest} containing the student's credentials
     * @return the unique internal identifier of the newly registered {@link StudentUser}
     * @throws RuntimeException if the required {@code ROLE_STUDENT} lookup fails in the database
     */
    @Transactional
    public StudentUser registerStudent(StudentUserRegistrationRequest request) {

        if(userService.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        StudentUser studentUser = StudentUser.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .roles(Set.of(roleRepository.findByType(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"))))
            .build();

        return studentUserRepository.save(studentUser);
    }

    /**
     * Registers a new teacher user in the database and assigns the mandatory teacher role.
     *
     * @param request the {@link TeacherUserRegistrationRequest} containing credentials and registration details
     * @return the unique internal identifier of the newly created {@link TeacherUser}
     * @throws RuntimeException if the required {@code ROLE_TEACHER} is not present in the database
     */
    @Transactional
    public TeacherUser registerTeacher(TeacherUserRegistrationRequest request) {
        
        if(userService.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        TeacherUser teacherUser = TeacherUser.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .roles(Set.of(roleRepository.findByType(RoleType.ROLE_TEACHER)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"))))
            .build();

        return teacherUserRepository.save(teacherUser);
    }
}
