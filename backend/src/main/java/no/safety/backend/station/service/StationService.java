package no.safety.backend.station.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.station.dto.StationDataResponse;
import no.safety.backend.station.dto.StationExerciseDataResponse;
import no.safety.backend.station.dto.StationRequest;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.dto.StudentStationDataResponse;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StationService {

  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final ExerciseRepository exerciseRepository;
  private final CompletedExerciseRepository completedExerciseRepository;

  @Transactional
  public StationResponse create(StationRequest request) {
    Station station = new Station();

    applyFields(station, request);
    return StationResponse.from(stationRepository.save(station));
  }

  @Transactional(readOnly = true)
  public List<StationResponse> getAllStations() {
    return stationRepository.findAll().stream()
        .map(StationResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public StationResponse getStationByUid(UUID uid) {
    return stationRepository.findByUid(uid)
        .map(StationResponse::from)
        .orElseThrow(() -> new NotFoundException("Station not found with uid: " + uid));
  }

  @Transactional(readOnly = true)
  public StudentStationDataResponse getMyStationData(StudentUser student) {
    if (student.getClassroom() == null || student.getClassroom().getWorldUid() == null) {
      return new StudentStationDataResponse(null, List.of());
    }

    World world = worldRepository.findByUid(student.getClassroom().getWorldUid())
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + student.getClassroom().getWorldUid()));
    Set<UUID> completedExerciseUids = getCompletedExerciseUids(student);

    boolean previousStationCompleted = true;
    List<StationDataResponse> stations = new java.util.ArrayList<>();

    for (UUID stationUid : world.getStationUids()) {
      Station station = require(stationUid);
      boolean isUnlocked = previousStationCompleted;
      StationDataResponse stationData = getStationData(station, completedExerciseUids, isUnlocked, false);
      stations.add(stationData);
      previousStationCompleted = stationData.isCompleted();
    }

    return new StudentStationDataResponse(WorldResponse.from(world), stations);
  }

  @Transactional(readOnly = true)
  public StationDataResponse getStationData(UUID uid, StudentUser student) {
    Station station = require(uid);
    Set<UUID> completedExerciseUids = getCompletedExerciseUids(student);

    return getStationData(station, completedExerciseUids, isStationUnlocked(station, student, completedExerciseUids), false);
  }

  @Transactional(readOnly = true)
  public StudentStationDataResponse getTeacherPreviewStationData(UUID worldUid) {
    World world = worldRepository.findByUid(worldUid)
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + worldUid));

    List<StationDataResponse> stations = world.getStationUids().stream()
        .map(stationUid -> getTeacherPreviewStationData(require(stationUid)))
        .toList();

    return new StudentStationDataResponse(WorldResponse.from(world), stations);
  }

  @Transactional(readOnly = true)
  public StationDataResponse getTeacherPreviewStationData(UUID stationUid, UUID worldUid) {
    Station station = require(stationUid);

    if (worldUid != null) {
      World world = worldRepository.findByUid(worldUid)
          .orElseThrow(() -> new NotFoundException("World not found with uid: " + worldUid));
      if (!world.getStationUids().contains(stationUid)) {
        throw new NotFoundException("Station not found in world: " + stationUid);
      }
    }

    return getTeacherPreviewStationData(station);
  }

  public boolean isStationComplete(Station station, Set<UUID> completedExerciseUids) {
    List<UUID> exerciseUids = station.getExerciseUids();
    return !exerciseUids.isEmpty() && exerciseUids.stream().allMatch(completedExerciseUids::contains);
  }

  @Transactional
  public StationResponse update(UUID uid, StationRequest request) {
    Station station = require(uid);

    applyFields(station, request);
    return StationResponse.from(station);
  }

  @Transactional
  public void delete(UUID uid) {
    stationRepository.delete(require(uid));
  }

  private void applyFields(Station station, StationRequest request) {
    station.setTitle(request.title());
    station.setDescription(request.description());
    station.setHint(request.hint());
    station.setImage(request.image());
    station.setBackgroundColor(request.backgroundColor());
    station.setExerciseUids(request.exerciseUids());
    station.setBoss(request.isBoss());
  }

  private Station require(UUID uid) {
    return stationRepository.findByUid(uid)
        .orElseThrow(() -> new NotFoundException("Station not found with uid: " + uid));
  }

  private StationDataResponse getStationData(
      Station station,
      Set<UUID> completedExerciseUids,
      boolean isUnlocked,
      boolean isTeacherPreview) {
    List<UUID> exerciseUids = station.getExerciseUids();
    List<Boolean> completedExercises = exerciseUids.stream()
        .map(completedExerciseUids::contains)
        .toList();
    int completedExerciseCount = (int) completedExercises.stream().filter(Boolean::booleanValue).count();

    return new StationDataResponse(
        station.getUid(),
        station.getTitle(),
        station.getDescription(),
        station.getHint(),
        station.getImage(),
        station.getBackgroundColor(),
        exerciseUids,
        station.isBoss(),
        completedExercises,
        completedExerciseCount,
        exerciseUids.size(),
        isStationComplete(station, completedExerciseUids),
        isUnlocked,
        isTeacherPreview,
        getExerciseData(station, completedExerciseUids, isTeacherPreview)
    );
  }

  private StationDataResponse getTeacherPreviewStationData(Station station) {
    Set<UUID> completedExerciseUids = Set.copyOf(station.getExerciseUids());
    return getStationData(station, completedExerciseUids, true, true);
  }

  private List<StationExerciseDataResponse> getExerciseData(
      Station station,
      Set<UUID> completedExerciseUids,
      boolean isTeacherPreview) {
    List<UUID> exerciseUids = station.getExerciseUids();

    return exerciseUids.stream()
        .map(exerciseUid -> {
          int index = exerciseUids.indexOf(exerciseUid);
          Exercise exercise = exerciseRepository.findByPublicId(exerciseUid)
              .orElseThrow(() -> new NotFoundException("Exercise not found with uid: " + exerciseUid));
          boolean isCompleted = completedExerciseUids.contains(exerciseUid);
          boolean isUnlocked = isTeacherPreview || index == 0 || completedExerciseUids.contains(exerciseUids.get(index - 1));

          return new StationExerciseDataResponse(
              exercise.getPublicId(),
              exercise.getTitle(),
              exercise.getDifficultyName(),
              exercise.getDescription(),
              isCompleted,
              isUnlocked
          );
        })
        .toList();
  }

  private boolean isStationUnlocked(Station station, StudentUser student, Set<UUID> completedExerciseUids) {
    if (student.getClassroom() == null || student.getClassroom().getWorldUid() == null) {
      return true;
    }

    World world = worldRepository.findByUid(student.getClassroom().getWorldUid())
        .orElseThrow(() -> new NotFoundException("World not found with uid: " + student.getClassroom().getWorldUid()));
    int stationIndex = world.getStationUids().indexOf(station.getUid());

    if (stationIndex <= 0) {
      return stationIndex == 0;
    }

    Station previousStation = require(world.getStationUids().get(stationIndex - 1));
    return isStationComplete(previousStation, completedExerciseUids);
  }

  private Set<UUID> getCompletedExerciseUids(StudentUser student) {
    return completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(student.getPublicUuid())
        .stream()
        .map(completedExercise -> completedExercise.getExerciseUid())
        .collect(java.util.stream.Collectors.toSet());
  }
}
