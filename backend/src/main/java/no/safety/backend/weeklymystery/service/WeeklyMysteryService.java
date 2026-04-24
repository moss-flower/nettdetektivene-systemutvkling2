package no.safety.backend.weeklymystery.service;

import java.nio.file.FileSystemException;
import java.time.LocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.domain.FilePersistenceException;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.service.UserService;
import no.safety.backend.utility.service.FileService;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;
import no.safety.backend.weeklymystery.model.WeeklyMysterySubmission;
import no.safety.backend.weeklymystery.repository.WeeklyMysteryPromptRepository;
import no.safety.backend.weeklymystery.repository.WeeklyMysterySubmissionRepository;

@Service
@RequiredArgsConstructor
public class WeeklyMysteryService {
    
    private final WeeklyMysteryPromptRepository promptRepository;
    private final WeeklyMysterySubmissionRepository submissionRepository;
    private final FileService fileService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    private final Logger logger = LoggerFactory.getLogger(WeeklyMysteryService.class);
    

    public void saveStudentSubmission(WeeklyMysterySubmissionCreateRequest request, Long studentId) {
        logger.debug("Save student submisison called with request " + request);
        //resolve items from storage
        WeeklyMysteryPrompt prompt = promptRepository.findByPublicUuid(request.weeklyMysterPromptUuid());
        StudentUser sender = (StudentUser) userService.findByInternalId(studentId);

        String fileName = request.file().getName();
        String storageKey = generateStorageKey(studentId, prompt.getInternalId(), fileName);
        
        //persist file
        fileService.save(request.file(), storageKey);
        

        // build submission
        WeeklyMysterySubmission submission = WeeklyMysterySubmission.builder()
        .message(request.message())
        .fileName(fileName)
        .storageKey(storageKey)
        .submitterStudentUser(sender)
        .weeklyMysteryPrompt(prompt)
        .build();

        try {
            submissionRepository.save(submission);
            NotificationEvent event = new NotificationEvent(sender.getPublicUuid(), "Elev " + sender.getUsername() + "har lastet opp et nytt inlegg på Ukens Mysterium", 0);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            logger.error("Error storing submission " + submission);
            fileService.delete(storageKey);
            throw new FilePersistenceException("Error persisting file, please try again.");
        } 
    } 

    private String generateStorageKey(Long userId, Long promptId, String fileName) {
        return "/" + userId.toString() + "/" + promptId.toString() + "/" + fileName;
    }

}
