package no.safety.backend.completedexercise.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for validating student progress and determining if exercises are locked.
 * Checks that previous exercises and stations are completed before allowing new ones.
 */
@Service
@RequiredArgsConstructor
public class ProgressValidationService {

  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final CompletedExerciseRepository completedExerciseRepository;

  /**
   * Checks if an exercise is locked for a student based on their progress.
   * An exercise is locked if the previous exercise in the station is not completed,
   * or if the previous station has not been fully completed.
   *
   * @param student the student attempting to complete the exercise
   * @param exerciseUid the UUID of the exercise to check
   * @return true if the exercise is locked, false otherwise
   * @throws NotFoundException if the exercise or station is not found in the student's world
   */
  @Transactional(readOnly = true)
  public boolean isLocked(StudentUser student, UUID exerciseUid) {
    if (student.getClassroom() == null) {
      return false;
    }

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
        Station previousStation = stationRepository.findByUid(previousStationUid)
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

  /**
   * Retrieves the world assigned to the student's classroom.
   *
   * @param student the student whose world to retrieve
   * @return the World entity
   * @throws NotFoundException if the classroom has no world or the world is not found
   */
  private World getStudentWorld(StudentUser student) {
    UUID worldUid = student.getClassroom().getWorldUid();
    if (worldUid == null) {
      throw new NotFoundException("Classroom has no assigned world");
    }

    return worldRepository.findByUid(worldUid)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + worldUid));
  }

  /**
   * Finds the station that contains the specified exercise in the student's world.
   *
   * @param world the world to search in
   * @param exerciseUid the exercise UUID to find
   * @return the Station containing the exercise
   * @throws NotFoundException if no station contains the exercise
   */
  private Station findStationInWorld(World world, UUID exerciseUid) {
    for (UUID stationUid : world.getStationUids()) {
      Station station = stationRepository.findByUid(stationUid)
          .orElseThrow(() -> new NotFoundException("Station not found with uid: " + stationUid));
      if (station.getExerciseUids().contains(exerciseUid)) {
        return station;
      }
    }

    throw new NotFoundException("No station found for exercise in student's world: " + exerciseUid);
  }
}
