package no.safety.backend.weeklymystery.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.service.ClassroomService;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.exception.domain.FilePersistenceException;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.service.UserService;
import no.safety.backend.utility.service.FileService;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptResponse;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;
import no.safety.backend.weeklymystery.model.WeeklyMysterySubmission;
import no.safety.backend.weeklymystery.repository.WeeklyMysteryPromptRepository;
import no.safety.backend.weeklymystery.repository.WeeklyMysterySubmissionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyMysteryService {
    
    private final WeeklyMysteryPromptRepository promptRepository;
    private final WeeklyMysterySubmissionRepository submissionRepository;
    private final FileService fileService;
    private final UserService userService;
    private final ClassroomService classroomService;
    private final ApplicationEventPublisher eventPublisher;

    public WeeklyMysteryPromptResponse getWeeklyMysteryPrompt(UUID classroomUuid) {
        log.debug("Get Weekly Mystery Prompt for " + classroomUuid);

        //Note, currently no validation whatsover that the user should actually have access to that classroom.
        //Need to refactor the classroom service.
        Classroom classroom = classroomService.getFullClassroom(classroomUuid);
        if(classroom.getWeeklyMysteryPrompt() == null) {
            throw new NotFoundException("No weekly prompt found");
        }
        return WeeklyMysteryPromptResponse.fromEntity(classroom.getWeeklyMysteryPrompt());
    }
    

    public void saveStudentSubmission(WeeklyMysterySubmissionCreateRequest request, Long studentId) {
        log.debug("Save student submisison called with request " + request);
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
            NotificationEvent event = new NotificationEvent(studentId, "Elev " + sender.getUsername() + "har lastet opp et nytt inlegg på Ukens Mysterium", 0);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Error storing submission " + submission);
            fileService.delete(storageKey);
            throw new FilePersistenceException("Error persisting file, please try again.");
        } 
    } 

    public WeeklyMysteryPromptResponse savePrompt(WeeklyMysteryPromptCreateRequest request, Long senderId) {
        TeacherUser sender = (TeacherUser) userService.findByInternalId(senderId);


        Set<Classroom> classrooms = sender.getClassrooms().stream().filter(
            e -> request.publicClassroomIds().contains(e.getUid()))
                .collect(Collectors.toSet());

        WeeklyMysteryPrompt prompt = WeeklyMysteryPrompt.builder()
            .weekLabel("")
            .title(request.title())
            .prompt(request.prompt())
            .clue(request.clue())
            .teacherUser(sender)
            .classrooms(classrooms)
            .build();
        promptRepository.save(prompt);

        return WeeklyMysteryPromptResponse.fromEntity(prompt);
    }

    public HashSet<WeeklyMysteryPromptResponse> fetchUsersPrompts(Long internalUserId) {
        HashSet<WeeklyMysteryPrompt> prompts = promptRepository.findAllByTeacher_id(internalUserId);

        HashSet<WeeklyMysteryPromptResponse> responses = new HashSet<>();

        for (WeeklyMysteryPrompt prompt : prompts) {
            responses.add(WeeklyMysteryPromptResponse.fromEntity(prompt));
        }
        return responses; 
    }

    private String generateStorageKey(Long userId, Long promptId, String fileName) {
        return "/" + userId.toString() + "/" + promptId.toString() + "/" + fileName;
    }

}
