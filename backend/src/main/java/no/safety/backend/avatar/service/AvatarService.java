package no.safety.backend.avatar.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final StudentUserRepository studentUserRepository;

    @Transactional
    public void updateAvatar(Long internalUserId, AvatarConfigRequest request) throws RuntimeException {
        StudentUser student = studentUserRepository.findById(internalUserId)
            .orElseThrow(() -> new RuntimeException("No student found with this Id"));
        
        student.setAvatarConfig(request.toEntity());
    }
    
}
