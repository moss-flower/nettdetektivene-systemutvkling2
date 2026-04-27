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
import no.safety.backend.leaderboard.dto.LeaderboardResponse;
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
     * Gets the rankings of all {@link StudentUser}s in the specified {@link Classroom} based on pagination.
     * @param pageable
     * @param classroomPublicUuid
     * @return
     */
    @GetMapping("/classroom/{classroomPublicUuid}")
    @Operation(summary = "Get top rankings", description = "Returns a paginated list of top scores")
    public ResponseEntity<Page<LeaderboardResponse>> getTopRankings(
            @PageableDefault(size = 20, sort = "xp", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable UUID classroomPublicUuid
        ) {

        Page<LeaderboardResponse> rankings = leaderboardService.getClassroomRankings(pageable, classroomPublicUuid);
        return ResponseEntity.ok(rankings);
    }

    /**
     * Gets the rank of the logged in {@link StudentUser} relative to the other students in their {@link Classroom}.
     * @param userPrincipal
     * @return
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('STUDENT')")
    @Operation(summary = "Get rank of the calling user.", description = "Returns the leaderboard stats for the user.")
    public ResponseEntity<LeaderboardResponse> getCallerUserRanking(
            @AuthenticationPrincipal UserPrincipal userPrincipal
        ) {

        return ResponseEntity.ok(leaderboardService.getUserStats(userPrincipal.getUserInternalId()));
    }
}