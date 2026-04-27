package no.safety.backend.leaderboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.leaderboard.model.ClassroomLeaderboardView;

public interface ClassroomLeaderboardViewRepository extends JpaRepository<ClassroomLeaderboardView, Long> {

}
