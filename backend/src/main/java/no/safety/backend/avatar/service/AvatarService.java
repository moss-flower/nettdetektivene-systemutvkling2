package no.safety.backend.avatar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.avatar.dto.AvatarConfigResponse;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

/**
 * Service class for managing student avatar configurations.
 * Provides business logic for retrieving and updating avatar settings.
 * All operations require a valid student user ID and are logged for debugging purposes.
 */
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final StudentUserRepository studentUserRepository;

    /**
     * Retrieves the avatar configuration for a specific student.
     *
     * @param internalUserId the internal ID of the student user
     * @return the avatar configuration response containing the student's avatar settings
     * @throws NotFoundException if no student is found with the given ID
     */
    public AvatarConfigResponse getAvatar(Long internalUserId) {
        logger.debug("AvatarService: Request made for read for user with internalid " + internalUserId);
        StudentUser student = studentUserRepository.findById(internalUserId)
            .orElseThrow(() -> new NotFoundException("No student found with this Id"));

        return AvatarConfigResponse.fromEntity(student.getAvatarConfig());
    }

    /**
     * Updates the avatar configuration for a specific student.
     * This operation is transactional - if any error occurs, the changes will be rolled back.
     *
     * @param internalUserId the internal ID of the student user
     * @param request the avatar configuration request containing the new settings
     * @throws NotFoundException if no student is found with the given ID
     */
    @Transactional
    public void updateAvatar(Long internalUserId, AvatarConfigRequest request) throws RuntimeException {
        logger.debug("AvatarService: Request made for update for user with internalid " + internalUserId);
        StudentUser student = studentUserRepository.findById(internalUserId)
            .orElseThrow(() -> new NotFoundException("No student found with this Id"));
        
        student.setAvatarConfig(request.toEntity());
    }
    
}
