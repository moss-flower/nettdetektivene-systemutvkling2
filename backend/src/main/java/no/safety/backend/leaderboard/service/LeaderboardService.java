package no.safety.backend.leaderboard.service;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.leaderboard.dto.UserLeaderboardResponse;
import no.safety.backend.leaderboard.repository.ClassroomLeaderboardRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * A service to handle leaderboards for all {@link StudentUser}'s in a
 * {@link Classroom}
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final StudentUserRepository studentUserRepository;

    private final ClassroomLeaderboardRepository classroomLeaderboardRepository;

    /**
     * Gets the leaderboard of all {@link Classroom}s based on pagination.
     * 
     * @param pageable
     * @return
     */
    @NonNull
    @Transactional(readOnly = true)
    public Page<ClassroomLeaderboardResponse> getClassroomRankings(@NonNull Pageable pageable) {
        return classroomLeaderboardRepository.findAllClassroomsTotalScores(pageable)
                .map(result -> new ClassroomLeaderboardResponse(
                        result.classroomPublicUuid(),
                        result.className(),
                        result.score(),
                        result.rank()));
    }

    /**
     * Gets the leaderboard stats for a single {@link StudentUser}
     * 
     * @param userInternalId
     * @param classroomPublicUuid
     * @return
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
}
