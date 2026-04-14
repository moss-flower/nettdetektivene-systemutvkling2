package no.safety.backend.registration.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.safety.backend.registration.dto.StudentUserRegistrationRequest;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRegistrationService {

    private final StudentUserRepository studentUserRepository;
    private final TeacherUserRepository teacherUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Long registerStudent(StudentUserRegistrationRequest request) {
        StudentUser studentUser = new StudentUser(null, request.username(), passwordEncoder.encode(request.password()));

        return studentUserRepository.save(studentUser).getInternalId();
    }

}
