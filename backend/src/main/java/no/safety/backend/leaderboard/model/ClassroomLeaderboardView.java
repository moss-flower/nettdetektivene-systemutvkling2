package no.safety.backend.leaderboard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@Subselect("""
            SELECT
                c.internal_id AS internal_id,
                c.name AS class_name,
                CAST(COALESCE(SUM(s.score), 0) AS SIGNED) AS total_score,
                DENSE_RANK() OVER (ORDER BY SUM(s.score) DESC) AS rank_position
            FROM classrooms c
            LEFT JOIN students s ON c.uid = s.classroom_uid
            GROUP BY c.internal_id, c.name
        """)
public class ClassroomLeaderboardView {

    @Id
    @Column(name = "internal_id")
    private Long internalId;

    @Column(name = "class_name")
    private String className;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "rank_position")
    private Long rankPosition;
}