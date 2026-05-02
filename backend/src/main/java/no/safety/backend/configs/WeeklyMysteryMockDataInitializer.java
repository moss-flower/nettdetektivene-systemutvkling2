package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.model.ClassroomWeeklyMystery;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.classroom.repository.ClassroomWeeklyMysteryRepository;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.weeklymystery.model.WeeklyMysteryPrompt;
import no.safety.backend.weeklymystery.model.WeeklyMysterySubmission;
import no.safety.backend.weeklymystery.repository.WeeklyMysteryPromptRepository;
import no.safety.backend.weeklymystery.repository.WeeklyMysterySubmissionRepository;

/**
 * Demo data for ukens mysterium: prompts, classroom assignment, history rows, and a few student submissions.
 * Targets Lisa's seeded classrooms 3-trinn A (Ada + Elias) and 7-trinn A (Ingrid + Emil).
 * Must run after ClassroomMockDataInitializer and GameplayMockDataInitializer (explicit {@code @Order} chain).
 * Uses the same public media object keys as gameplay seed tasks so image URLs resolve when MinIO is populated.
 */
@Component
@Profile("!test")
@Order(20)
@RequiredArgsConstructor
@Slf4j
public class WeeklyMysteryMockDataInitializer implements ApplicationRunner {

  public static final UUID SEED_PROMPT_ACTIVE_3A = stableUuid("weekly-mystery-seed:active-3a");
  public static final UUID SEED_PROMPT_ARCHIVED_3A = stableUuid("weekly-mystery-seed:archived-3a");
  public static final UUID SEED_PROMPT_LIBRARY_LISA = stableUuid("weekly-mystery-seed:library-lisa");

  public static final UUID SEED_SUB_ADA_NEWS = stableUuid("weekly-mystery-seed:sub-ada-news");
  public static final UUID SEED_SUB_ELIAS_PHOTO = stableUuid("weekly-mystery-seed:sub-elias-photo");
  public static final UUID SEED_SUB_ADA_SOCIAL = stableUuid("weekly-mystery-seed:sub-ada-social");

  public static final UUID SEED_PROMPT_ACTIVE_7A = stableUuid("weekly-mystery-seed:active-7a");
  public static final UUID SEED_PROMPT_ARCHIVED_7A = stableUuid("weekly-mystery-seed:archived-7a");
  public static final UUID SEED_SUB_INGRID_7A = stableUuid("weekly-mystery-seed:sub-ingrid-7a");
  public static final UUID SEED_SUB_EMIL_7A = stableUuid("weekly-mystery-seed:sub-emil-7a");

  private static final String SEED_IMAGE_NEWS = "seed/tasks/news-warning.svg";
  private static final String SEED_IMAGE_PHOTO = "seed/tasks/photo-warning.svg";
  private static final String SEED_IMAGE_SOCIAL = "seed/tasks/social-warning.svg";
  private static final String SEED_IMAGE_EMAIL = "seed/tasks/email-warning.svg";

  private final TeacherUserRepository teacherUserRepository;
  private final StudentUserRepository studentUserRepository;
  private final ClassroomRepository classroomRepository;
  private final WeeklyMysteryPromptRepository promptRepository;
  private final WeeklyMysterySubmissionRepository submissionRepository;
  private final ClassroomWeeklyMysteryRepository classroomWeeklyMysteryRepository;
  private final ExerciseRepository exerciseRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    log.info("WeeklyMysteryMockDataInitializer: running (expects klasserom and ovinger seeded first).");

    Optional<TeacherUser> lisa = teacherUserRepository.findByEmail("lisa.laerer@test.com");
    if (lisa.isEmpty()) {
      log.warn("WeeklyMysteryMockDataInitializer: demo teacher lisa.laerer@test.com not found — skipping weekly mystery seed.");
      return;
    }

    TeacherUser teacher = lisa.get();
    Optional<Exercise> newsExercise = exerciseRepository.findByPublicId(GameplayMockDataInitializer.NEWSSTAND_EXERCISE_1_UID);

    upsertPrompt(
        SEED_PROMPT_LIBRARY_LISA,
        teacher,
        "Kladd",
        "Mistenkelig chat",
        "Be elevene sende inn et skjermbilde av en chat som virker mistenkelig, og forklare hvorfor.",
        "Se etter ukjente navn, lenker og press for å svare fort.",
        Optional.empty()
    );

    seedKlasse3a(teacher, newsExercise);
    seedKlasse7a(teacher, newsExercise);
  }

  private void seedKlasse3a(TeacherUser teacher, Optional<Exercise> newsExercise) {
    Optional<Classroom> classroomOpt = classroomRepository.findByUid(ClassroomMockDataInitializer.KLASSE_3A_UID);
    if (classroomOpt.isEmpty()) {
      log.warn("WeeklyMysteryMockDataInitializer: demo classroom 3-trinn A not found — skipping.");
      return;
    }

    Classroom classroom = classroomOpt.get();
    StudentUser ada = studentUserRepository.findByEmail("ada.elev@test.com").orElse(null);
    StudentUser elias = studentUserRepository.findByEmail("elias.elev@test.com").orElse(null);

    WeeklyMysteryPrompt archived = upsertPrompt(
        SEED_PROMPT_ARCHIVED_3A,
        teacher,
        "Uke 12",
        "Tenk før du klikker",
        "Hva er det smarteste du kan gjøre hvis en melding virker for god til å være sann?",
        "Tenk på avsender, språk og om noen prøver å skynde på deg.",
        Optional.empty()
    );

    WeeklyMysteryPrompt active = upsertPrompt(
        SEED_PROMPT_ACTIVE_3A,
        teacher,
        "Uke 13",
        "Finn falske spor i nyhetsbilder",
        "Finn noe som kan være falske nyheter eller et bilde som prøver å lure deg. "
            + "Send inn skjermbilde og forklar kort hva du reagerer på.",
        "Sjekk avsender, dato og om teksten prøver å sjokkere deg.",
        newsExercise
    );

    recordHistoryIfMissing(classroom, archived, LocalDate.now().minusWeeks(1));
    recordHistoryIfMissing(classroom, active, LocalDate.now().minusDays(2));

    /*
     * Always point the klasserom at the seeded active prompt. Earlier we only did this when the field was
     * null, which left old assignments in place while submissions were tied to the demo prompt UUID — then
     * the teacher UI (which loads innleveranser for the current prompt) showed nothing.
     */
    classroom.setWeeklyMysteryPrompt(active);
    classroomRepository.save(classroom);

    if (ada != null) {
      upsertSubmission(SEED_SUB_ADA_NEWS, active, ada,
          "Avisartikkel som bruker skumle og vage ord uten god kilde.",
          "fake_headline.svg",
          SEED_IMAGE_NEWS);
      upsertSubmission(SEED_SUB_ADA_SOCIAL, active, ada,
          "Denne profilen lover gratis ting hvis du deler videre — virker litt for bra.",
          "social_bait.svg",
          SEED_IMAGE_SOCIAL);
    } else {
      log.warn("WeeklyMysteryMockDataInitializer: ada.elev@test.com not found — no Ada submissions seeded.");
    }

    if (elias != null) {
      upsertSubmission(SEED_SUB_ELIAS_PHOTO, active, elias,
          "Bildet ser redigert ut ved kantene, og det mangler foto-kreditering.",
          "edited_photo.svg",
          SEED_IMAGE_PHOTO);
    } else {
      log.warn("WeeklyMysteryMockDataInitializer: elias.elev@test.com not found — no Elias submissions seeded.");
    }

    int submissionRows = (ada != null ? 2 : 0) + (elias != null ? 1 : 0);
    log.info("WeeklyMysteryMockDataInitializer: demo data for {} — aktivt oppdrag «{}» ({} innlevering(er)).",
        classroom.getName(), active.getTitle(), submissionRows);
  }

  private void seedKlasse7a(TeacherUser teacher, Optional<Exercise> newsExercise) {
    Optional<Classroom> classroomOpt = classroomRepository.findByUid(ClassroomMockDataInitializer.KLASSE_7A_UID);
    if (classroomOpt.isEmpty()) {
      log.warn("WeeklyMysteryMockDataInitializer: demo classroom 7-trinn A not found — skipping.");
      return;
    }

    Classroom classroom = classroomOpt.get();
    StudentUser ingrid = studentUserRepository.findByEmail("ingrid.elev@test.com").orElse(null);
    StudentUser emil = studentUserRepository.findByEmail("emil.elev@test.com").orElse(null);

    WeeklyMysteryPrompt archived = upsertPrompt(
        SEED_PROMPT_ARCHIVED_7A,
        teacher,
        "Uke 11",
        "Nett og personvern",
        "Hva kan du gjøre hvis noen ber om passordet ditt i en melding?",
        "Et trygt tips er å si nei og spørre en voksen du stoler på.",
        Optional.empty()
    );

    WeeklyMysteryPrompt active = upsertPrompt(
        SEED_PROMPT_ACTIVE_7A,
        teacher,
        "Uke 12",
        "E-post som virker ekte",
        "Send inn et skjermbilde av en e-post eller melding du synes virker ekte eller mistenkelig, og forklar hvorfor.",
        "Se etter avsenderadresse, stress og rare lenker.",
        newsExercise
    );

    recordHistoryIfMissing(classroom, archived, LocalDate.now().minusWeeks(2));
    recordHistoryIfMissing(classroom, active, LocalDate.now().minusDays(1));

    classroom.setWeeklyMysteryPrompt(active);
    classroomRepository.save(classroom);

    if (ingrid != null) {
      upsertSubmission(SEED_SUB_INGRID_7A, active, ingrid,
          "Avsenderen sier de er fra skolen, men adressen ser feil ut.",
          "skole_epost.svg",
          SEED_IMAGE_EMAIL);
    }
    if (emil != null) {
      upsertSubmission(SEED_SUB_EMIL_7A, active, emil,
          "De ber meg klikke med en gang — det føles stresset.",
          "stresset_lenke.svg",
          SEED_IMAGE_EMAIL);
    }

    log.info("WeeklyMysteryMockDataInitializer: demo data for {} — aktivt oppdrag {}.",
        classroom.getName(), active.getTitle());
  }

  private void recordHistoryIfMissing(Classroom classroom, WeeklyMysteryPrompt prompt, LocalDate assignedDate) {
    if (classroomWeeklyMysteryRepository.existsByClassroom_InternalIdAndWeeklyMysteryPrompt_PublicUuid(
        classroom.getInternalId(), prompt.getPublicUuid())) {
      return;
    }
    ClassroomWeeklyMystery row = new ClassroomWeeklyMystery();
    row.setClassroom(classroom);
    row.setWeeklyMysteryPrompt(prompt);
    row.setAssignedDate(assignedDate);
    classroomWeeklyMysteryRepository.save(row);
  }

  private WeeklyMysteryPrompt upsertPrompt(UUID publicUuid, TeacherUser teacher, String weekLabel,
      String title, String promptText, String clue, Optional<Exercise> exerciseOpt) {
    WeeklyMysteryPrompt entity =
        promptRepository.existsByPublicUuid(publicUuid)
            ? promptRepository.findByPublicUuid(publicUuid)
            : WeeklyMysteryPrompt.builder().publicUuid(publicUuid).build();

    entity.setWeekLabel(weekLabel);
    entity.setTitle(title);
    entity.setPrompt(promptText);
    entity.setClue(clue);
    entity.setTeacherUser(teacher);
    exerciseOpt.ifPresent(entity::setExercise);

    return promptRepository.save(entity);
  }

  private void upsertSubmission(UUID publicUuid, WeeklyMysteryPrompt prompt, StudentUser student,
      String message, String fileName, String storageKey) {
    WeeklyMysterySubmission entity = submissionRepository.findByPublicUuid(publicUuid)
        .orElseGet(() -> WeeklyMysterySubmission.builder().publicUuid(publicUuid).build());

    entity.setMessage(message);
    entity.setFileName(fileName);
    entity.setStorageKey(storageKey);
    entity.setSubmitterStudentUser(student);
    entity.setWeeklyMysteryPrompt(prompt);
    submissionRepository.save(entity);
  }

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
