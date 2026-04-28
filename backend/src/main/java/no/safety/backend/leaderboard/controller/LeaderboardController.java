package no.safety.backend.leaderboard.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.leaderboard.dto.UserLeaderboardResponse;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.leaderboard.service.LeaderboardService;
import no.safety.backend.user.model.StudentUser;

/**
 * Leaderboard controller that defines API endpoints for getting leaderboards.
 */
@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * Gets the sorted leaderboard of all {@link Classroom}s based on pagination.
     * @param pageable
     * @return
     */
    @GetMapping("/classroom")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @Operation(summary = "Get rankings of classrooms", description = "Returns a paginated list of the total scores for all classrooms")
    public ResponseEntity<Page<ClassroomLeaderboardResponse>> getTopRankings(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable
        ) {

        Page<ClassroomLeaderboardResponse> rankings = leaderboardService.getClassroomRankings(pageable);
        return ResponseEntity.ok(rankings);
    }

    /**
     * Gets the rank of the logged in {@link StudentUser} relative to the other students in their {@link Classroom}.
     * @param userPrincipal
     * @return
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get rank of the calling user.", description = "Returns the leaderboard stats for the user.")
    public ResponseEntity<UserLeaderboardResponse> getCallerUserRanking(
            @AuthenticationPrincipal UserPrincipal userPrincipal
        ) {

        return ResponseEntity.ok(leaderboardService.getUserStats(userPrincipal.getUserInternalId()));
    }
}