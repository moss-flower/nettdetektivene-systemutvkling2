package no.safety.backend.completedexercise.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.exception.domain.StudentNotInClassException;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgressValidationServiceTest {

  @Mock
  private StationRepository stationRepository;

  @Mock
  private WorldRepository worldRepository;

  @Mock
  private CompletedExerciseRepository completedExerciseRepository;

  @InjectMocks
  private ProgressValidationService progressValidationService;

  @Test
  void isLocked_returnsTrueWhenPreviousExerciseNotCompleted() {
    UUID studentUid = UUID.randomUUID();
    UUID worldUid = UUID.randomUUID();
    UUID stationUid = UUID.randomUUID();
    UUID exercise1 = UUID.randomUUID();
    UUID exercise2 = UUID.randomUUID();

    StudentUser student = studentWithClassroom(studentUid, worldUid);
    World world = worldWithStations(stationUid);
    Station station = stationWithExercises(stationUid, exercise1, exercise2);

    when(worldRepository.findByUid(worldUid)).thenReturn(Optional.of(world));
    when(stationRepository.findByUid(stationUid)).thenReturn(Optional.of(station));
    when(completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exercise1))
        .thenReturn(false);

    boolean locked = progressValidationService.isLocked(student, exercise2);

    assertTrue(locked);
  }

  @Test
  void isLocked_returnsFalseWhenFirstExerciseAndPreviousStationCompleted() {
    UUID studentUid = UUID.randomUUID();
    UUID worldUid = UUID.randomUUID();
    UUID station1Uid = UUID.randomUUID();
    UUID station2Uid = UUID.randomUUID();
    UUID station1Exercise1 = UUID.randomUUID();
    UUID station1Exercise2 = UUID.randomUUID();
    UUID station2Exercise1 = UUID.randomUUID();

    StudentUser student = studentWithClassroom(studentUid, worldUid);
    World world = worldWithStations(station1Uid, station2Uid);
    Station station1 = stationWithExercises(station1Uid, station1Exercise1, station1Exercise2);
    Station station2 = stationWithExercises(station2Uid, station2Exercise1);

    when(worldRepository.findByUid(worldUid)).thenReturn(Optional.of(world));
    when(stationRepository.findByUid(station1Uid)).thenReturn(Optional.of(station1));
    when(stationRepository.findByUid(station2Uid)).thenReturn(Optional.of(station2));
    when(completedExerciseRepository.countByStudentUidAndExerciseUidIn(eq(studentUid), anyCollection()))
        .thenReturn(2L);

    boolean locked = progressValidationService.isLocked(student, station2Exercise1);

    assertFalse(locked);
  }

  @Test
  void isLocked_throwsWhenStudentNotInClass() {
    StudentUser student = StudentUser.builder()
        .publicUuid(UUID.randomUUID())
        .build();

    assertThrows(
        StudentNotInClassException.class,
        () -> progressValidationService.isLocked(student, UUID.randomUUID())
    );
  }

  @Test
  void isLocked_throwsWhenExerciseNotInWorld() {
    UUID studentUid = UUID.randomUUID();
    UUID worldUid = UUID.randomUUID();
    UUID stationUid = UUID.randomUUID();
    UUID unknownExercise = UUID.randomUUID();

    StudentUser student = studentWithClassroom(studentUid, worldUid);
    World world = worldWithStations(stationUid);
    Station station = stationWithExercises(stationUid, UUID.randomUUID());

    when(worldRepository.findByUid(worldUid)).thenReturn(Optional.of(world));
    when(stationRepository.findByUid(stationUid)).thenReturn(Optional.of(station));

    assertThrows(
        NotFoundException.class,
        () -> progressValidationService.isLocked(student, unknownExercise)
    );
  }

  private static StudentUser studentWithClassroom(UUID studentUid, UUID worldUid) {
    Classroom classroom = new Classroom();
    classroom.setWorldUid(worldUid);

    StudentUser student = StudentUser.builder()
        .publicUuid(studentUid)
        .build();
    student.setClassroom(classroom);
    return student;
  }

  private static World worldWithStations(UUID... stationUids) {
    World world = new World();
    world.setStationUids(List.of(stationUids));
    return world;
  }

  private static Station stationWithExercises(UUID stationUid, UUID... exercises) {
    Station station = new Station();
    station.setUid(stationUid);
    station.setExerciseUids(List.of(exercises));
    return station;
  }
}
