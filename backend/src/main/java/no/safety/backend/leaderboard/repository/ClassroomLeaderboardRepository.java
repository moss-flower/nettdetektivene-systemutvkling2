package no.safety.backend.leaderboard.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;

public interface ClassroomLeaderboardRepository extends JpaRepository<Classroom, Long> {
    @Query(value = """
            SELECT new no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse(
                c.uid,
                c.name,
                COALESCE(SUM(m.score), 0L),
                DENSE_RANK() OVER (ORDER BY COALESCE(SUM(m.score), 0L) DESC)
            )
            FROM Classroom c
            LEFT JOIN c.members m
            GROUP BY c.internalId, c.uid, c.name
            ORDER BY COALESCE(SUM(m.score), 0L) DESC, c.name ASC
            """, countQuery = "SELECT COUNT(c) FROM Classroom c")
    Page<ClassroomLeaderboardResponse> findAllClassroomsTotalScores(@NonNull Pageable pageable);
}
