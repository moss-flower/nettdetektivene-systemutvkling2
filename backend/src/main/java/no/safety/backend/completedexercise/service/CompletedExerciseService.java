package no.safety.backend.completedexercise.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.service.AchievementService;
import no.safety.backend.completedexercise.dto.CompletedExerciseRequest;
import no.safety.backend.completedexercise.dto.CompletedExerciseResponse;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.notepad.dto.NotepadEntryCreateRequest;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.service.NotepadEntryService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompletedExerciseService {

  private final CompletedExerciseRepository completedExerciseRepository;
  private final ProgressValidationService progressValidationService;
  private final StudentUserRepository studentUserRepository;
  private final StationRepository stationRepository;
  private final AchievementService achievementService;
  private final ExerciseRepository exerciseRepository;
  private final NotepadEntryService notepadEntryService;

  @Transactional
  public CompletedExerciseResponse create(CompletedExerciseRequest request, StudentUser student) {
    UUID studentUid = student.getPublicUuid();
    UUID exerciseUid = request.exerciseUid();

    if (progressValidationService.isLocked(student, exerciseUid)) {
      throw new ForbiddenException("Oppgaven er låst. Fullfør forrige oppgave eller stasjon først.");
    }

    boolean hasCompletedExerciseFromBefore = completedExerciseRepository.existsByStudentUidAndExerciseUid(studentUid, exerciseUid);
    student.addScore(hasCompletedExerciseFromBefore ? 10 : 100);
    studentUserRepository.save(student);

    if (hasCompletedExerciseFromBefore) {
      return CompletedExerciseResponse.from(completedExerciseRepository.findByStudentUidAndExerciseUid(studentUid, exerciseUid)
          .orElseThrow());
    }

    CompletedExercise completedExercise = new CompletedExercise();
    completedExercise.setStudentUid(studentUid);
    completedExercise.setExerciseUid(exerciseUid);
 
  
    CompletedExercise saved = completedExerciseRepository.save(completedExercise);

    checkAndUnlockStationAchievement(student, exerciseUid);
    createExerciseNotepadEntryIfConfigured(student, exerciseUid);

    return CompletedExerciseResponse.from(saved);
  }

  @Transactional(readOnly = true)
  public List<CompletedExerciseResponse> getMyCompletedExercises(StudentUser student) {
    return completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(student.getPublicUuid())
        .stream()
        .map(CompletedExerciseResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<CompletedExerciseResponse> getCompletedExercisesForStudent(UUID studentUid) {
    return completedExerciseRepository.findAllByStudentUidOrderByExerciseUidAsc(studentUid)
        .stream()
        .map(CompletedExerciseResponse::from)
        .toList();
  }

  private void checkAndUnlockStationAchievement(StudentUser student, UUID exerciseUid) {
    Optional<Station> stationOpt = stationRepository.findByExerciseUid(exerciseUid);
    if (stationOpt.isEmpty()) {
      return;
    }

    Station station = stationOpt.get();

    Set<UUID> completedUids = completedExerciseRepository
        .findAllByStudentUidOrderByExerciseUidAsc(student.getPublicUuid()).stream()
        .map(CompletedExercise::getExerciseUid)
        .collect(Collectors.toSet());

    boolean isComplete = station.getExerciseUids().stream()
        .allMatch(completedUids::contains);

    if (!isComplete) {
      return;
    }

    achievementService.unlockStationAchievement(student, station);
  }

  private void createExerciseNotepadEntryIfConfigured(StudentUser student, UUID exerciseUid) {
    Exercise exercise = exerciseRepository.findByPublicId(exerciseUid)
        .orElseThrow(() -> new NotFoundException("Exercise not found"));

    NotepadEntry notepadEntry = exercise.getNotepadEntry();
    if (notepadEntry == null) {
      return;
    }

    notepadEntryService.create(student.getInternalId(), new NotepadEntryCreateRequest(
        notepadEntry.getTitle(),
        notepadEntry.getType(),
        notepadEntry.getContent(),
        notepadEntry.getSource()
    ));

  }
}
