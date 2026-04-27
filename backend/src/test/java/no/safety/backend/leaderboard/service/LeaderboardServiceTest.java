package no.safety.backend.leaderboard.service;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.leaderboard.dto.UserLeaderboardResponse;
import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private StudentUserRepository studentUserRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void shouldReturnPaginatedRankingsWithCorrectGlobalRank() {
        UUID classroomPublicUuid = UUID.randomUUID();
        int pageSize = 2;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        StudentUser studentUser1 = createStudent("Student 1", 10);
        StudentUser studentUser2 = createStudent("Student 2", 5);
        
        List<StudentUser> students = List.of(studentUser1, studentUser2);
        Page<StudentUser> studentPage = new PageImpl<>(students, pageable, 20);

        given(studentUserRepository.findAllByClassroomUid(classroomPublicUuid, pageable))
                .willReturn(studentPage);

        Page<ClassroomLeaderboardResponse> result = leaderboardService.getClassroomRankings(pageable);

        assertThat(result.getContent()).hasSize(2);
        
        assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
        assertThat(result.getContent().get(0).className()).isEqualTo("Class 1");

        assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
    }
    


    @Test
    void shouldReturnUserStatsSuccessfully() {
        Long stundentUserInternalId = 1L;
        Long classroomInternalId = 1L;
        int score = 10;
        Long expectedRank = 5L;

        Classroom classroom = mock(Classroom.class);
        given(classroom.getInternalId()).willReturn(classroomInternalId);

        StudentUser studentUser = createStudent("Student 1", score);
        studentUser.setClassroom(classroom);

        given(studentUserRepository.findById(stundentUserInternalId)).willReturn(Optional.of(studentUser));
        given(studentUserRepository.findRankByClassroomInternalIdAndScore(classroomInternalId, score))
                .willReturn(Optional.of(expectedRank));

        UserLeaderboardResponse response = leaderboardService.getUserStats(stundentUserInternalId);

        assertThat(response.username()).isEqualTo("Student 1");
        assertThat(response.score()).isEqualTo(score);
        assertThat(response.rank()).isEqualTo(expectedRank);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long studentUserInternalId = -1L;
        given(studentUserRepository.findById(studentUserInternalId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> leaderboardService.getUserStats(studentUserInternalId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldThrowExceptionWhenClassroomIsNull() {
        Long studentUserInternalId = 1L;
        StudentUser student = createStudent("Student 1", 10);
        student.setClassroom(null);

        given(studentUserRepository.findById(studentUserInternalId)).willReturn(Optional.of(student));

        assertThatThrownBy(() -> leaderboardService.getUserStats(studentUserInternalId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User classroom not found");
    }

    @Test
    void shouldThrowExceptionWhenRankCalculationFails() {
        Long studentUserInternalId = 1L;
        Long classroomInternalId = -1L;
        Classroom classroom = mock(Classroom.class);
        given(classroom.getInternalId()).willReturn(classroomInternalId);

        StudentUser studentUser = createStudent("Student 1", 10);
        studentUser.setClassroom(classroom);

        given(studentUserRepository.findById(studentUserInternalId)).willReturn(Optional.of(studentUser));
        given(studentUserRepository.findRankByClassroomInternalIdAndScore(classroomInternalId, 10))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> leaderboardService.getUserStats(studentUserInternalId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not get user rank");
    }


    private StudentUser createStudent(String username, int score) {
        StudentUser studentUser = new StudentUser();
        studentUser.setUsername(username);
        studentUser.setScore(score);
        return studentUser;
    }
}
