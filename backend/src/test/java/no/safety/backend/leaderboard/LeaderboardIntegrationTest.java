package no.safety.backend.leaderboard;

import no.safety.backend.BaseIntegrationTest;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.leaderboard.repository.ClassroomLeaderboardRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional 
class LeaderboardIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClassroomLeaderboardRepository repository;

    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private StudentUserRepository studentUserRepository;

    @Test
    void shouldCalculateClassroomTotalScoreCorrectly() {
        Classroom classroom = new Classroom();
        classroom.setName("Class 1");
        classroom.setCode("Classcode");
        classroom = classroomRepository.save(classroom);

        StudentUser student1 = new StudentUser();
        student1.setUsername("student1");
        student1.setEmail("student1@email.com");
        student1.setPassword("password");
        student1.setClassroom(classroom);
        student1.setScore(10);

        StudentUser student2 = new StudentUser();
        student2.setUsername("student2");
        student2.setEmail("student2@email.com");
        student2.setPassword("password");
        student2.setClassroom(classroom);
        student2.setScore(20);

        studentUserRepository.save(student1);
        studentUserRepository.save(student2);

        Page<ClassroomLeaderboardResponse> result = repository.findAllClassroomsTotalScores(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).score()).isEqualTo(30);
    }

    @Test
    void shouldReturnCorrectlyRankedClassrooms() {
        Classroom classroom = new Classroom();
        classroom.setName("Class 1");
        classroom.setCode("Classcode");
        classroom = classroomRepository.save(classroom);

        StudentUser student1 = new StudentUser();
        student1.setUsername("student1");
        student1.setEmail("student1@email.com");
        student1.setPassword("password");
        student1.setClassroom(classroom);
        student1.setScore(10);

        Classroom classroom2 = new Classroom();
        classroom2.setName("Class 2");
        classroom2.setCode("Classcode2");
        classroom2 = classroomRepository.save(classroom2);

        StudentUser student2 = new StudentUser();
        student2.setUsername("student2");
        student2.setEmail("student2@email.com");
        student2.setPassword("password");
        student2.setClassroom(classroom2);
        student2.setScore(20);

        studentUserRepository.save(student1);
        studentUserRepository.save(student2);

        Page<ClassroomLeaderboardResponse> result = repository.findAllClassroomsTotalScores(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).className()).isEqualTo("Class 2");
        assertThat(result.getContent().get(0).score()).isEqualTo(20);
        assertThat(result.getContent().get(0).rank()).isEqualTo(1);
        assertThat(result.getContent().get(1).className()).isEqualTo("Class 1");
        assertThat(result.getContent().get(1).score()).isEqualTo(10);
        assertThat(result.getContent().get(1).rank()).isEqualTo(2);
    }


}