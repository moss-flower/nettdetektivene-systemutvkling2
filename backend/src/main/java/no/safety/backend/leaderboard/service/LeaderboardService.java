package no.safety.backend.leaderboard.service;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.leaderboard.dto.LeaderboardResponse;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * A service to handle leaderboards for all {@link StudentUser}'s in a {@link Classroom}
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final StudentUserRepository studentUserRepository;

    /**
     * Gets the leaderboard for all users in the {@link Classroom} based on pagination.
     * @param pageable
     * @param classroomPublicUuid
     * @return
     */
    @NonNull
    public Page<LeaderboardResponse> getClassroomRankings(@NonNull Pageable pageable, @NonNull UUID classroomPublicUuid){
        Page<StudentUser> studentUserPage = studentUserRepository.findAllByClassroomUid(classroomPublicUuid, pageable);

        return studentUserPage.map(studentUser -> {
            int indexOnPage = studentUserPage.getContent().indexOf(studentUser);
            long globalRank = (long) pageable.getPageNumber() * pageable.getPageSize() + indexOnPage + 1;
            return LeaderboardResponse.from(studentUser, globalRank);
    });
    }

    /**
     * Gets the leaderboard stats for a single {@link StudentUser}
     * @param userInternalId
     * @param classroomPublicUuid
     * @return
     */
    @NonNull
    public LeaderboardResponse getUserStats(@NonNull Long userInternalId){
        StudentUser studentUser = studentUserRepository.findById(userInternalId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (studentUser.getClassroom() == null){
            throw new NotFoundException("User classroom not found.");
        }

        Long studentUserClassroomInternalId = studentUser.getClassroom().getInternalId();

        Long studentUserRank = studentUserRepository.findRankByClassroomInternalIdAndScore(studentUserClassroomInternalId, studentUser.getScore())
                .orElseThrow(() -> new RuntimeException("Could not get user rank."));

        return LeaderboardResponse.from(studentUser, studentUserRank);
    }
}
