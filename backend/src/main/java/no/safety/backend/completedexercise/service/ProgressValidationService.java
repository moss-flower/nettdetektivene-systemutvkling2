package no.safety.backend.completedexercise.service;

import java.util.List;
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

@Service
@RequiredArgsConstructor
public class ProgressValidationService {

  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final CompletedExerciseRepository completedExerciseRepository;

  @Transactional(readOnly = true)
  public boolean isLocked(StudentUser student, String exerciseId) {
    Station station = stationRepository.findByExerciseUid(exerciseId)
        .orElseThrow(() -> new NotFoundException("No station found for exercise: " + exerciseId));

    List<String> exercisesInStation = station.getExerciseUids();
    int exerciseIndex = exercisesInStation.indexOf(exerciseId);

    if (exerciseIndex > 0) {
      String previousExerciseId = exercisesInStation.get(exerciseIndex - 1);
      boolean prevCompleted = completedExerciseRepository.existsByStudentAndExerciseId(student, previousExerciseId);
      if (!prevCompleted) {
        return true; 
      }
    } else {
      World world = worldRepository.findByStationUid(station.getUid())
          .orElseThrow(() -> new NotFoundException("No world found for station: " + station.getUid()));

      List<String> stationsInWorld = world.getStationUids();
      int stationIndex = stationsInWorld.indexOf(station.getUid());

      if (stationIndex > 0) {
        String previousStationUid = stationsInWorld.get(stationIndex - 1);
        Station previousStation = stationRepository.findById(previousStationUid)
            .orElseThrow(() -> new NotFoundException("Previous station not found: " + previousStationUid));

        List<String> prevExercises = previousStation.getExerciseUids();
        if (!prevExercises.isEmpty()) {
          long completedCount = completedExerciseRepository.countByStudentAndExerciseIdIn(student, prevExercises);
          if (completedCount < prevExercises.size()) {
            return true; 
          }
        }
      }
    }

    return false;
  }
}
