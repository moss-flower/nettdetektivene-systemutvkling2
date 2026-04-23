package no.safety.backend.completedexercise.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.exception.domain.StudentNotInClassException;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressValidationService {

  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final CompletedExerciseRepository completedExerciseRepository;

  @Transactional(readOnly = true)
  public boolean isLocked(StudentUser student, UUID exerciseUid) {
    UUID studentUid = student.getPublicUuid();
    World world = getStudentWorld(student);
    Station station = findStationInWorld(world, exerciseUid);

    List<UUID> exercisesInStation = station.getExerciseUids();
    int exerciseIndex = exercisesInStation.indexOf(exerciseUid);

    if (exerciseIndex < 0) {
      throw new NotFoundException("Exercise not found in student's world: " + exerciseUid);
    }

    if (exerciseIndex > 0) {
      UUID previousExerciseUid = exercisesInStation.get(exerciseIndex - 1);
      boolean prevCompleted = completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, previousExerciseUid);
      if (!prevCompleted) {
        return true;
      }
    } else {
      List<UUID> stationsInWorld = world.getStationUids();
      int stationIndex = stationsInWorld.indexOf(station.getUid());

      if (stationIndex < 0) {
        throw new NotFoundException("Station not found in student's world: " + station.getUid());
      }

      if (stationIndex > 0) {
        UUID previousStationUid = stationsInWorld.get(stationIndex - 1);
        Station previousStation = stationRepository.findById(previousStationUid)
            .orElseThrow(() -> new NotFoundException("Previous station not found: " + previousStationUid));

        List<UUID> prevExercises = previousStation.getExerciseUids();
        if (!prevExercises.isEmpty()) {
          long completedCount = completedExerciseRepository.countByStudentUidAndExerciseUidIn(studentUid, prevExercises);
          if (completedCount < prevExercises.size()) {
            return true;
          }
        }
      }
    }

    return false;
  }

  private World getStudentWorld(StudentUser student) {
    if (student.getClassroom() == null) {
      throw new StudentNotInClassException("Student is not in a classroom");
    }

    UUID worldUid = student.getClassroom().getWorldUid();
    if (worldUid == null) {
      throw new NotFoundException("Classroom has no assigned world");
    }

    return worldRepository.findById(worldUid)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + worldUid));
  }

  private Station findStationInWorld(World world, UUID exerciseUid) {
    for (UUID stationUid : world.getStationUids()) {
      Station station = stationRepository.findById(stationUid)
          .orElseThrow(() -> new NotFoundException("Station not found with uid: " + stationUid));
      if (station.getExerciseUids().contains(exerciseUid)) {
        return station;
      }
    }

    throw new NotFoundException("No station found for exercise in student's world: " + exerciseUid);
  }
}
