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
            SELECT
                c.uid AS classroomPublicUuid,
                c.name AS className,
                COALESCE(SUM(m.score), 0) AS score,
                DENSE_RANK() OVER (ORDER BY SUM(m.score) DESC) AS rank
            FROM Classroom c
            LEFT JOIN c.members m
            GROUP BY c.internalId, c.uid, c.name
            """, nativeQuery = true)
    Page<ClassroomLeaderboardResponse> findAllClassroomsTotalScores(@NonNull Pageable pageable);
}
