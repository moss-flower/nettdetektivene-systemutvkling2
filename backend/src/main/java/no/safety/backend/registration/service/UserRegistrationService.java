package no.safety.backend.registration.service;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.registration.dto.TeacherUserRegistrationRequest;
import no.safety.backend.user.model.RoleType;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.RoleRepository;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRegistrationService {

    private final StudentUserRepository studentUserRepository;
    private final TeacherUserRepository teacherUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Long registerStudent(StudentUserRegistrationRequest request) {
        StudentUser studentUser = StudentUser.builder()
            .username(request.username())
            .email(null)
            .password(passwordEncoder.encode(request.password()))
            .roles(Set.of(roleRepository.findByType(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Role not found"))))
            .build();

        return studentUserRepository.save(studentUser).getInternalId();
    }

    public Long registerTeacher(TeacherUserRegistrationRequest request) {
        TeacherUser teacherUser = TeacherUser.builder()
            .username(request.username())
            .email(null)
            .password(passwordEncoder.encode(request.password()))
            .roles(Set.of(roleRepository.findByType(RoleType.ROLE_TEACHER)
                .orElseThrow(() -> new RuntimeException("Role not found"))))
            .build();

        return teacherUserRepository.save(teacherUser).getInternalId();
    }

}
