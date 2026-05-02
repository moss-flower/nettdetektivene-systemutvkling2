package no.safety.backend.leaderboard.service;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.leaderboard.dto.UserLeaderboardResponse;
import no.safety.backend.leaderboard.repository.ClassroomLeaderboardRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service class for managing leaderboard data.
 * Handles retrieval of classroom rankings and student leaderboard statistics.
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final StudentUserRepository studentUserRepository;

    private final ClassroomRepository classroomRepository;

    private final ClassroomLeaderboardRepository classroomLeaderboardRepository;

    /**
     * Retrieves a paginated leaderboard of all classrooms ranked by total score.
     *
     * @param pageable pagination information
     * @return a page of classroom leaderboard responses
     */
    @NonNull
    @Transactional(readOnly = true)
    public Page<ClassroomLeaderboardResponse> getClassroomRankings(@NonNull Pageable pageable) {
        return classroomLeaderboardRepository.findAllClassroomsTotalScores(pageable)
                .map(result -> new ClassroomLeaderboardResponse(
                        result.getClassroomPublicUuid(),
                        result.getClassName(),
                        result.getScore(),
                        result.getRank()));
    }

    /**
     * Retrieves leaderboard statistics for a specific student.
     * Returns the student's score and rank within their classroom.
     *
     * @param userInternalId the internal ID of the student
     * @return the user's leaderboard response with score and rank
     * @throws NotFoundException if the user or classroom is not found
     */
    @NonNull
    @Transactional(readOnly = true)
    public UserLeaderboardResponse getUserStats(@NonNull Long userInternalId) {
        StudentUser studentUser = studentUserRepository.findById(userInternalId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (studentUser.getClassroom() == null) {
            throw new NotFoundException("User classroom not found.");
        }

        Long studentUserClassroomInternalId = studentUser.getClassroom().getInternalId();

        Long studentUserRank = studentUserRepository
                .findRankByClassroomInternalIdAndScore(studentUserClassroomInternalId, studentUser.getScore())
                .orElseThrow(() -> new RuntimeException("Could not get user rank."));

        return UserLeaderboardResponse.from(studentUser, studentUserRank);
    }

    /**
     * Retrieves a paginated leaderboard of all students in a classroom.
     * Only the classroom owner (teacher) can access this data.
     *
     * @param callerUserInternalId the internal ID of the calling teacher
     * @param classroomUid the public UUID of the classroom
     * @param pageable pagination information
     * @return a page of student leaderboard responses
     * @throws NotFoundException if the classroom is not found
     * @throws ForbiddenException if the caller is not the classroom owner
     */
    @NonNull
    @Transactional(readOnly = true)
    public Page<UserLeaderboardResponse> getStudentRankings(
            @NonNull Long callerUserInternalId,
            @NonNull UUID classroomUid,
            @NonNull Pageable pageable) {
        Classroom classroom = classroomRepository.findByUid(classroomUid)
                .orElseThrow(() -> new NotFoundException("Classroom not found."));

        boolean isOwner = classroom.getOwners().stream()
                .anyMatch(owner -> owner.getInternalId().equals(callerUserInternalId));

        if (!isOwner) {
            throw new ForbiddenException("Teacher does not have access to this classroom.");
        }

        Page<StudentUser> studentUserPage = studentUserRepository.findAllByClassroomUidAndClassroomPendingFalse(classroomUid, pageable);

        return studentUserPage.map(studentUser -> {
            int indexOnPage = studentUserPage.getContent().indexOf(studentUser);
            long globalRank = (long) pageable.getPageNumber() * pageable.getPageSize() + indexOnPage + 1;
            return UserLeaderboardResponse.from(studentUser, globalRank);
        });
    }
}
