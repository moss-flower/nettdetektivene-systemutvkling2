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
import no.safety.backend.leaderboard.dto.UserLeaderboardResponse;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.leaderboard.service.LeaderboardService;

/**
 * REST controller for leaderboard endpoints.
 * Provides endpoints for retrieving classroom rankings and student leaderboard statistics.
 */
@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * Retrieves a paginated leaderboard of all classrooms ranked by total score.
     *
     * @param pageable pagination information (default size 20, sorted by score descending)
     * @return a page of classroom leaderboard responses
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
     * Retrieves the leaderboard statistics for the authenticated student.
     * Returns the student's score and rank within their classroom.
     *
     * @param userPrincipal the authenticated user principal
     * @return the user's leaderboard response with score and rank
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get rank of the calling user.", description = "Returns the leaderboard stats for the user.")
    public ResponseEntity<UserLeaderboardResponse> getCallerUserRanking(
            @AuthenticationPrincipal UserPrincipal userPrincipal
        ) {

        return ResponseEntity.ok(leaderboardService.getUserStats(userPrincipal.getUserInternalId()));
    }

    /**
     * Retrieves a paginated leaderboard of all students in a specific classroom.
     * Only the classroom owner (teacher) can access this endpoint.
     *
     * @param userPrincipal the authenticated teacher principal
     * @param classroomUid the public UUID of the classroom
     * @param pageable pagination information (default size 20, sorted by score descending)
     * @return a page of student leaderboard responses
     */
    @GetMapping("/{classroomUid}/students")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get leaderboard of all students in classroom.", description = "Returns the leaderboard ranking the students in the given class.")
    public ResponseEntity<Page<UserLeaderboardResponse>> getCallerUserRanking(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID classroomUid,
            @PageableDefault(size = 20, sort = "score", direction = Sort.Direction.DESC) Pageable pageable
        ) {

        return ResponseEntity.ok(leaderboardService.getStudentRankings(userPrincipal.getUserInternalId(), classroomUid, pageable));
    }
}