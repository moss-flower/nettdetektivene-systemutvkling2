package no.safety.backend.leaderboard.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardProjection;

public interface ClassroomLeaderboardRepository extends JpaRepository<Classroom, Long> {
    @Query(value = """
            SELECT
                c.uid as classroomPublicUuid,
                c.name as className,
                COALESCE(SUM(m.score), 0L) as score,
                DENSE_RANK() OVER (ORDER BY COALESCE(SUM(m.score), 0L) DESC) as rank
            FROM Classroom c
            LEFT JOIN c.members m
            GROUP BY c.internalId, c.uid, c.name
            ORDER BY COALESCE(SUM(m.score), 0L) DESC, c.name ASC
            """, countQuery = "SELECT COUNT(c) FROM Classroom c")
    Page<ClassroomLeaderboardProjection> findAllClassroomsTotalScores(@NonNull Pageable pageable);
}
