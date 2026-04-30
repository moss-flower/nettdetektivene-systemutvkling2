package no.safety.backend.weeklymystery.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.safety.backend.classroom.model.ClassroomWeeklyMystery;
import no.safety.backend.classroom.repository.ClassroomWeeklyMysteryRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.exception.AccessDeniedException;
import no.safety.backend.exception.domain.FilePersistenceException;
import no.safety.backend.media.dto.MediaUploadResponse;
import no.safety.backend.media.service.MediaStorageService;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.dto.WeeklyMysterySubmissionEvent;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.service.UserService;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptResponse;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionResponse;
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
    private final MediaStorageService mediaStorageService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final ClassroomWeeklyMysteryRepository classroomWeeklyMysteryRepository;

    /**
     * Saves a student's submission for a Weekly Mystery prompt.
     * Persists the attached file to storage, then saves the submission entity.
     * If the database save fails, the file is deleted from storage to avoid orphaned files.
     *
     * @param request   the submission request containing the prompt UUID, message, and file
     * @param studentId the internal ID of the submitting student
     * @throws FilePersistenceException if the submission cannot be saved
     */
    public void saveStudentSubmission(WeeklyMysterySubmissionCreateRequest request, Long studentId) {
        log.debug("Save student submission called with request {}", request);

        WeeklyMysteryPrompt prompt = promptRepository.findByPublicUuid(request.weeklyMysterPromptUuid());
        StudentUser sender = (StudentUser) userService.findByInternalId(studentId);

        // getOriginalFilename() is safer than getName(), which can return the full path on some systems
        String fileName = request.file().getOriginalFilename() != null
                ? request.file().getOriginalFilename()
                : "unknown_file";
        String folder = generateStorageFolder(studentId, prompt.getInternalId());
        MediaUploadResponse uploadedFile = mediaStorageService.uploadPublicImage(request.file(), folder);
        String storageKey = uploadedFile.objectKey();

        WeeklyMysterySubmission submission = WeeklyMysterySubmission.builder()
                .message(request.message())
                .fileName(fileName)
                .storageKey(storageKey)
                .submitterStudentUser(sender)
                .weeklyMysteryPrompt(prompt)
                .build();

        try {
            submissionRepository.save(submission);
            NotificationEvent event = new WeeklyMysterySubmissionEvent(
                    studentId,
                    "Elev " + sender.getUsername() + " har lastet opp et nytt innlegg på Ukens Mysterium"
            );
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Error storing submission {}", submission, e);
            mediaStorageService.deletePublicObject(storageKey);
            throw new FilePersistenceException("Error persisting file, please try again.");
        }
    }

    /**
     * Retrieves all submissions for a given Weekly Mystery prompt,
     * mapping each to a {@link WeeklyMysterySubmissionResponse} including the associated file.
     *
     * @param promptUid the public UUID of the Weekly Mystery prompt
     * @return a set of submission responses, or an empty set if none exist
     */
    public Set<WeeklyMysterySubmissionResponse> getWeeklyMysterySubmissionResponses(UUID promptUid) {
        return submissionRepository.getByWeeklyMysteryPrompt_publicUuid(promptUid)
                .stream()
                .map(submission -> new WeeklyMysterySubmissionResponse(
                        submission.getPublicUuid(),
                        submission.getMessage(),
                        submission.getSubmitterStudentUser().getPublicUuid(),
                        submission.getFileName(),
                        mediaStorageService.publicUrl(submission.getStorageKey())
                ))
                .collect(Collectors.toSet());
    }

    /**
     * Deletes a student's submission if the requesting user is either the submitting student
     * or a teacher with access to the classroom the prompt is assigned to.
     * Removes the associated file from storage and the submission entity from the database.
     * If the database deletion fails, the file is left intact to avoid data loss.
     *
     * @param submissionUid the public UUID of the submission to delete
     * @param requesterId   the internal ID of the requesting user
     * @throws AccessDeniedException   if the requester is neither the submitter nor an authorized teacher
     * @throws EntityNotFoundException if no submission is found for the given UUID
     */
    @Transactional
    public void deleteSubmission(UUID submissionUid, Long requesterId) {
        WeeklyMysterySubmission submission = submissionRepository.findByPublicUuid(submissionUid)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found: " + submissionUid));
        boolean isSubmitter = submission.getSubmitterStudentUser().getInternalId().equals(requesterId);
        boolean isAuthorizedTeacher = classroomWeeklyMysteryRepository
                .existsByWeeklyMysteryPrompt_AndClassroom_Owners_InternalId(
                    submission.getWeeklyMysteryPrompt(), requesterId
                );

        if (!isSubmitter && !isAuthorizedTeacher) {
            throw new AccessDeniedException("User does not have permission to delete this submission.");
        }

        try {
            submissionRepository.delete(submission);
            mediaStorageService.deletePublicObject(submission.getStorageKey());
        } catch (Exception e) {
            log.error("Error deleting submission {}", submissionUid, e);
            throw new FilePersistenceException("Error deleting submission, please try again.");
        }
    }

    /**
     * Saves a new Weekly Mystery prompt created by a teacher.
     *
     * @param request  the prompt creation request containing title, prompt text, and clue
     * @param senderId the internal ID of the creating teacher
     * @return a response DTO representing the saved prompt
     */
    public WeeklyMysteryPromptResponse savePrompt(WeeklyMysteryPromptCreateRequest request, Long senderId) {
        TeacherUser sender = (TeacherUser) userService.findByInternalId(senderId);

        WeeklyMysteryPrompt prompt = WeeklyMysteryPrompt.builder()
                .weekLabel("")
                .title(request.title())
                .prompt(request.prompt())
                .clue(request.clue())
                .teacherUser(sender)
                .build();
        promptRepository.save(prompt);

        return WeeklyMysteryPromptResponse.fromEntity(prompt);
    }

    /**
     * Fetches all Weekly Mystery prompts created by a given teacher.
     *
     * @param internalUserId the internal ID of the teacher
     * @return a set of prompt response DTOs
     */
    public HashSet<WeeklyMysteryPromptResponse> fetchUsersPrompts(Long internalUserId) {
        HashSet<WeeklyMysteryPrompt> prompts = promptRepository.findAllByTeacherUser_internalId(internalUserId);

        HashSet<WeeklyMysteryPromptResponse> responses = new HashSet<>();
        for (WeeklyMysteryPrompt prompt : prompts) {
            responses.add(WeeklyMysteryPromptResponse.fromEntity(prompt));
        }
        return responses;
    }

    /**
     * Fetches a single Weekly Mystery prompt by its public UUID.
     *
     * @param promptId the public UUID of the prompt
     * @return the matching {@link WeeklyMysteryPrompt} entity
     */
    public WeeklyMysteryPrompt fetch(UUID promptId) {
        return promptRepository.findByPublicUuid(promptId);
    }

    /**
     * Deletes a Weekly Mystery prompt, verifying that it belongs to the requesting teacher.
     * No-op if the prompt is not found or does not belong to the teacher.
     *
     * @param promptId  the public UUID of the prompt to delete
     * @param teacherId the internal ID of the requesting teacher
     */
    @Transactional
    public void delete(UUID promptId, Long teacherId) {
        Set<WeeklyMysteryPrompt> teacherPrompts = promptRepository.findAllByTeacherUser_internalId(teacherId);

        // Fixed: was using == for UUID comparison, which compares references not values
        teacherPrompts.stream()
                .filter(p -> p.getPublicUuid().equals(promptId))
                .findFirst()
                .ifPresent(promptRepository::delete);
    }

    /**
     * Assigns a Weekly Mystery prompt to a classroom, recording the assignment date.
     *
     * @param classroom the classroom to assign the prompt to
     * @param prompt    the prompt to assign
     */
    @Transactional
    public void updateWeeklyMystery(Classroom classroom, WeeklyMysteryPrompt prompt) {
        ClassroomWeeklyMystery mystery = new ClassroomWeeklyMystery();
        mystery.setWeeklyMysteryPrompt(prompt);
        mystery.setClassroom(classroom);
        mystery.setAssignedDate(LocalDate.now());
        classroomWeeklyMysteryRepository.save(mystery);
    }

    /**
     * Generates a storage key for a file based on the user ID, prompt ID, and file name.
     * Format: /{userId}/{promptId}/{fileName}
     *
     * @param userId   the internal ID of the user
     * @param promptId the internal ID of the prompt
     * @param fileName the name of the file
     * @return the generated storage key
     */
    private String generateStorageFolder(Long userId, Long promptId) {
        return "uploads/mysteries/submissions/" + userId + "/" + promptId;
    }
}
