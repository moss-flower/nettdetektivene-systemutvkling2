package no.safety.backend.leaderboard.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardProjection;

/**
 * Repository for retrieving classroom leaderboard data.
 * Uses a custom JPQL query with window functions to calculate rankings.
 */
public interface ClassroomLeaderboardRepository extends JpaRepository<Classroom, Long> {
    /**
     * Finds all classrooms with their total scores and ranks.
     * Uses DENSE_RANK() window function to rank classrooms by total score.
     *
     * @param pageable pagination information
     * @return a page of ClassroomLeaderboardProjection with classroom data, scores, and ranks
     */
    @Query(value = """
            SELECT
                c.uid as classroomPublicUuid,
                c.name as className,
                COALESCE(SUM(m.score), 0L) as score,
                DENSE_RANK() OVER (ORDER BY COALESCE(SUM(m.score), 0L) DESC) as rank
            FROM Classroom c
            LEFT JOIN c.members m WITH m.classroomPending = false
            GROUP BY c.internalId, c.uid, c.name
            ORDER BY COALESCE(SUM(m.score), 0L) DESC, c.name ASC
            """, countQuery = "SELECT COUNT(c) FROM Classroom c")
    Page<ClassroomLeaderboardProjection> findAllClassroomsTotalScores(@NonNull Pageable pageable);
}
