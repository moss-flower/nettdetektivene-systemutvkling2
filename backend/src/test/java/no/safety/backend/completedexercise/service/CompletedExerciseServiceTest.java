package no.safety.backend.completedexercise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.notepad.service.NotepadEntryService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletedExerciseServiceTest {

  @Mock
  private CompletedExerciseRepository completedExerciseRepository;

  @Mock
  private ProgressValidationService progressValidationService;

  @Mock
  private StudentUserRepository studentUserRepository;

  @Mock
  private StationRepository stationRepository;

  @Mock
  private AchievementService achievementService;
  
  @Mock
  private ExerciseRepository exerciseRepository;

  @Mock
  private NotepadEntryService notepadEntryService;

  @InjectMocks
  private CompletedExerciseService completedExerciseService;

  @Test
  void create_whenExerciseLocked_throwsForbidden() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(true);

    assertThrows(ForbiddenException.class, () -> completedExerciseService.create(request, student));

    verify(progressValidationService).isLocked(student, exerciseUid);
    verifyNoInteractions(completedExerciseRepository);
  }

  @Test
  void create_whenAlreadyCompleted_returnsExistingResponse() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(true);
    CompletedExercise completed = new CompletedExercise();
    completed.setStudentUid(studentUid);
    completed.setExerciseUid(exerciseUid);
    when(completedExerciseRepository.findByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(java.util.Optional.of(completed));

    CompletedExerciseResponse response = completedExerciseService.create(request, student);

    assertEquals(studentUid, response.studentUid());
    assertEquals(exerciseUid, response.exerciseUid());
    verify(progressValidationService).isLocked(student, exerciseUid);
    verify(completedExerciseRepository).existsByStudentUidAndExerciseUid(studentUid, exerciseUid);
    verify(studentUserRepository).save(student);
    verifyNoInteractions(exerciseRepository, notepadEntryService);
  }

  @Test
  void create_whenValidWithoutNotepadEntry_savesAndReturnsResponse() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);
    Exercise exercise = new Exercise();
    exercise.setPublicId(exerciseUid);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(false);
    when(completedExerciseRepository.save(any(CompletedExercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(exerciseRepository.findByPublicId(exerciseUid)).thenReturn(Optional.of(exercise));

    CompletedExerciseResponse response = completedExerciseService.create(request, student);

    assertEquals(studentUid, response.studentUid());
    assertEquals(exerciseUid, response.exerciseUid());
    verify(completedExerciseRepository).save(any(CompletedExercise.class));
    verify(studentUserRepository).save(student);
    verify(exerciseRepository).findByPublicId(exerciseUid);
    verify(notepadEntryService, never()).create(any(), any());
  }

  @Test
  void create_whenValidWithNotepadEntry_createsNotepadEntry() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);
    Exercise exercise = new Exercise();
    exercise.setPublicId(exerciseUid);
    NotepadEntry notepadEntry = new NotepadEntry();
    notepadEntry.setTitle("Tip title");
    notepadEntry.setType(NotepadEntryType.TIP);
    notepadEntry.setContent("Tip content");
    notepadEntry.setSource("Exercise source");
    exercise.setNotepadEntry(notepadEntry);
    student.setInternalId(12L);

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(false);
    when(completedExerciseRepository.save(any(CompletedExercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(exerciseRepository.findByPublicId(exerciseUid)).thenReturn(Optional.of(exercise));

    completedExerciseService.create(request, student);

    verify(notepadEntryService).create(any(), any());
  }

  @Test
  void create_whenBossStationComplete_unlocksSpecialAchievementOnly() {
    UUID studentUid = UUID.randomUUID();
    UUID exerciseUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    CompletedExerciseRequest request = new CompletedExerciseRequest(exerciseUid);
    Exercise exercise = new Exercise();
    exercise.setPublicId(exerciseUid);
    Station station = new Station();
    station.setBoss(true);
    station.setTitle("Boss station");
    station.setExerciseUids(List.of(exerciseUid));

    when(progressValidationService.isLocked(student, exerciseUid)).thenReturn(false);
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid))
        .thenReturn(false);
    when(completedExerciseRepository.save(any(CompletedExercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(studentUid))
        .thenAnswer(invocation -> {
          CompletedExercise completed = new CompletedExercise();
          completed.setStudentUid(studentUid);
          completed.setExerciseUid(exerciseUid);
          return List.of(completed);
        });
    when(stationRepository.findByExerciseUid(exerciseUid)).thenReturn(Optional.of(station));
    when(exerciseRepository.findByPublicId(exerciseUid)).thenReturn(Optional.of(exercise));

    completedExerciseService.create(request, student);

    verify(achievementService).unlockAchievementForStudent(student, "Boss station", AchievementCategory.SPECIAL);
    verify(achievementService, never()).unlockAchievementForStudent(student, "Boss station", AchievementCategory.STOP);
  }

  @Test
  void getMyCompletedExercises_returnsMappedResponses() {
    UUID studentUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder().publicUuid(studentUid).build();
    UUID exercise1 = UUID.randomUUID();
    UUID exercise2 = UUID.randomUUID();

    CompletedExercise completed1 = new CompletedExercise();
    completed1.setStudentUid(studentUid);
    completed1.setExerciseUid(exercise1);

    CompletedExercise completed2 = new CompletedExercise();
    completed2.setStudentUid(studentUid);
    completed2.setExerciseUid(exercise2);

    when(completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(studentUid))
        .thenReturn(List.of(completed1, completed2));

    List<CompletedExerciseResponse> response = completedExerciseService.getMyCompletedExercises(student);

    assertEquals(2, response.size());
    assertEquals(exercise1, response.get(0).exerciseUid());
    assertEquals(exercise2, response.get(1).exerciseUid());
    verify(completedExerciseRepository).findAllByStudentUidOrderByExerciseUidAsc(studentUid);
  }
}
