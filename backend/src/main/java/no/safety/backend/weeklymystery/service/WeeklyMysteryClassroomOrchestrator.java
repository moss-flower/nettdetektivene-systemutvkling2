package no.safety.backend.weeklymystery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.model.ClassroomWeeklyMystery;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.classroom.repository.ClassroomWeeklyMysteryRepository;
import no.safety.backend.classroom.service.ClassroomService;
import no.safety.backend.exception.AccessDeniedException;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.service.UserService;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptResponse;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Provides an orchestration layer to help coordinate tasks, in place of an event system.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeeklyMysteryClassroomOrchestrator {

    private final WeeklyMysteryService weeklyMysteryService;
    private final ClassroomService classroomService;
    private final ClassroomRepository classroomRepository;
    private final UserService userService;
    private final ClassroomWeeklyMysteryRepository classroomWeeklyMysteryRepository;

    public void updateWeeklyMysteryForClassroom(UUID classroomPublicId,
                                                UUID weeklyMysteryPublicId,
                                                Long callerId) {
        log.debug("WeeklyMysteryClassroom Orchestrator: Update called with caller id " + callerId);
        /*
        Step 1. Retrieve object(s)
        step 2. verify that the specific caller *can* modify those objects
        step 3. update
         */

        Classroom classroom = classroomService.getFullClassroom(classroomPublicId);
        WeeklyMysteryPrompt prompt = weeklyMysteryService.fetch(weeklyMysteryPublicId);
        TeacherUser teacher = (TeacherUser) userService.findByInternalId(callerId);
        //Note, we could verify here, but verification is handled on the method call in the controller
        //This means by the time we get here the user is already verified ot be a teacher.
        //What we do need to verify is that they are in fact allowed to edit this classroom.

        Set<Classroom> teacherClassrooms = classroomRepository.findAllByOwners_PublicUuid(teacher.getPublicUuid());
        if(!teacherClassrooms.contains(classroom)) {
            log.error("WeeklyMysteryClassroomOrchestrator: Access Denied Error");
            throw new AccessDeniedException("Access denied: Cannot set Weekly Mystery for a classroom you don't own.");
        }

        classroom.setWeeklyMysteryPrompt(prompt);
        classroomService.updateWeeklyMystery(classroom, prompt);
        weeklyMysteryService.updateWeeklyMystery(classroom, prompt);
    }

    public List<WeeklyMysteryPromptResponse> getWeeklyMysteryHistory(UUID classroomPublicId) {
        Long classroomId = classroomService.getFullClassroom(classroomPublicId).getInternalId();
        List<ClassroomWeeklyMystery> mysteries =
                classroomWeeklyMysteryRepository.findByClassroom_InternalIdOrderByAssignedDateDesc(classroomId);
        List<WeeklyMysteryPromptResponse> responses = new ArrayList<>();
        for (ClassroomWeeklyMystery mystery : mysteries) {
            responses.add(WeeklyMysteryPromptResponse.fromEntity(mystery.getWeeklyMysteryPrompt()));
        }
        return responses;
    }

    
}
