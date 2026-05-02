package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.HashSet;
import java.util.Locale;
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
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.media.service.MediaStorageService;
import no.safety.backend.task.content.MultipleChoiceContent;
import no.safety.backend.task.content.TaskContent;
import no.safety.backend.task.content.XOrYContent;
import no.safety.backend.task.model.Task;
import no.safety.backend.task.repository.TaskRepository;
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

  public static final UUID SEED_EXERCISE_3A = stableUuid("weekly-mystery-seed:exercise-3a");
  public static final UUID SEED_EXERCISE_7A = stableUuid("weekly-mystery-seed:exercise-7a");

  private static final String SEED_IMAGE_NEWS = "seed/tasks/nyhetskvartalet/warning-gratis-mobil.png";
  private static final String SEED_IMAGE_PHOTO = "seed/tasks/fotografen/ai-folded-hands-many-fingers.jpeg";
  private static final String SEED_IMAGE_SOCIAL = "seed/tasks/sosiale-moteplassen/bad-alle-skoler-stenger-neste-uke.jpeg";
  private static final String SEED_IMAGE_SOCIAL_FACT = "seed/tasks/sosiale-moteplassen/good-udir-skoler-stenger-ikke.jpeg";
  private static final String SEED_IMAGE_EMAIL = "seed/tasks/postkontoret/fake-your-account-will-be-closed.jpeg";
  private static final String SEED_IMAGE_EMAIL_SAFE = "seed/tasks/postkontoret/real-timeplan-next-week.png";

  private final TeacherUserRepository teacherUserRepository;
  private final StudentUserRepository studentUserRepository;
  private final ClassroomRepository classroomRepository;
  private final WeeklyMysteryPromptRepository promptRepository;
  private final WeeklyMysterySubmissionRepository submissionRepository;
  private final ClassroomWeeklyMysteryRepository classroomWeeklyMysteryRepository;
  private final ExerciseRepository exerciseRepository;
  private final TaskRepository taskRepository;
  private final MediaStorageService mediaStorageService;

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
    Exercise classroom3aExercise = upsertExercise3a();
    Exercise classroom7aExercise = upsertExercise7a();

    upsertPrompt(
        SEED_PROMPT_LIBRARY_LISA,
        teacher,
        "Kladd",
        "Mistenkelig chat",
        "Be elevene sende inn et skjermbilde av en chat som virker mistenkelig, og forklare hvorfor.",
        "Se etter ukjente navn, lenker og press for å svare fort.",
        Optional.empty()
    );

    seedKlasse3a(teacher, classroom3aExercise);
    seedKlasse7a(teacher, classroom7aExercise);
  }

  private void seedKlasse3a(TeacherUser teacher, Exercise mysteryExercise) {
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
        "Skolene stenger neste uke?",
        "Se på innlegg om at skolene stenger neste uke. Finn ut hva som gjør at du bør sjekke saken før du tror på den.",
        "Se etter hvem som står bak, og om noen prøver å få deg til å reagere raskt.",
        Optional.of(mysteryExercise)
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
          "Dette innlegget sier at skolene stenger, men jeg vil sjekke om det faktisk kommer fra noen jeg kan stole på.",
          "skolene-stenger.jpeg",
          seedSubmissionImage("uploads/mysteries/submissions/seed/ada/skolene-stenger.jpeg", SEED_IMAGE_SOCIAL));
      upsertSubmission(SEED_SUB_ADA_SOCIAL, active, ada,
          "Jeg reagerte på at innlegget vil at alle skal dele fort, i stedet for å forklare rolig hva som skjer.",
          "udir-faktasjekk.jpeg",
          seedSubmissionImage("uploads/mysteries/submissions/seed/ada/udir-faktasjekk.jpeg", SEED_IMAGE_SOCIAL_FACT));
    } else {
      log.warn("WeeklyMysteryMockDataInitializer: ada.elev@test.com not found — no Ada submissions seeded.");
    }

    if (elias != null) {
      upsertSubmission(SEED_SUB_ELIAS_PHOTO, active, elias,
          "Jeg la merke til at dette virker mer som et rykte enn en vanlig beskjed fra skolen.",
          "skole-rykte.jpeg",
          seedSubmissionImage("uploads/mysteries/submissions/seed/elias/skole-rykte.jpeg", SEED_IMAGE_SOCIAL));
    } else {
      log.warn("WeeklyMysteryMockDataInitializer: elias.elev@test.com not found — no Elias submissions seeded.");
    }

    int submissionRows = (ada != null ? 2 : 0) + (elias != null ? 1 : 0);
    log.info("WeeklyMysteryMockDataInitializer: demo data for {} — aktivt oppdrag «{}» ({} innlevering(er)).",
        classroom.getName(), active.getTitle(), submissionRows);
  }

  private void seedKlasse7a(TeacherUser teacher, Exercise mysteryExercise) {
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
        "Melding fra skolekontoen",
        "Se på en melding som sier at skolekontoen må oppdateres. Finn ut hvilke detaljer som gjør den trygg eller mistenkelig.",
        "Se etter avsender, press og om meldingen prøver å få deg til å handle fort.",
        Optional.of(mysteryExercise)
    );

    recordHistoryIfMissing(classroom, archived, LocalDate.now().minusWeeks(2));
    recordHistoryIfMissing(classroom, active, LocalDate.now().minusDays(1));

    classroom.setWeeklyMysteryPrompt(active);
    classroomRepository.save(classroom);

    if (ingrid != null) {
      upsertSubmission(SEED_SUB_INGRID_7A, active, ingrid,
          "Avsenderen høres skoleaktig ut, men adressen og måten den prøver å stresse meg på gjør meg skeptisk.",
          "skolekonto-varsel.jpeg",
          seedSubmissionImage("uploads/mysteries/submissions/seed/ingrid/skolekonto-varsel.jpeg", SEED_IMAGE_EMAIL));
    }
    if (emil != null) {
      upsertSubmission(SEED_SUB_EMIL_7A, active, emil,
          "Dette ser mer ut som en vanlig timeplanmelding fordi det ikke prøver å skremme meg til å klikke.",
          "timeplan-neste-uke.png",
          seedSubmissionImage("uploads/mysteries/submissions/seed/emil/timeplan-neste-uke.png", SEED_IMAGE_EMAIL_SAFE));
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

  private Exercise upsertExercise3a() {
    Exercise exercise = exerciseRepository.findByPublicId(SEED_EXERCISE_3A).orElseGet(Exercise::new);
    exercise.setPublicId(SEED_EXERCISE_3A);
    exercise.setTitle("Ukens mysterium: skoleinnlegg");
    exercise.setDifficultyName("Lett");
    exercise.setDescription("Se på et innlegg om skolen og finn ut hva som gjør at du bør sjekke det nærmere.");
    exercise.setHint("Se etter om noen prøver å få deg til å dele raskt, og om det står tydelig hvem som står bak.");
    exercise.setNotepadEntry(defaultNotepadEntry("Ukens mysterium: skoleinnlegg",
        "Stopp opp hvis et innlegg prøver å få deg til å reagere fort."));

    if (exercise.getInternalId() != null) {
      taskRepository.deleteByExerciseId(exercise.getInternalId());
    }

    exercise.getTasks().clear();
    exercise.getTasks().addAll(new HashSet<>(List.of(
        multipleChoiceTask(
            exercise,
            "Hva bør få deg til å stoppe opp i dette innlegget?",
            "Bra. Når et innlegg prøver å få deg til å dele raskt, bør du sjekke mer før du tror på det.",
            "Se etter det som prøver å presse fram en rask reaksjon.",
            "Hva skurrer mest?",
            "Velg det tydeligste faresignalet i innlegget.",
            SEED_IMAGE_SOCIAL,
            "Et innlegg som sier at skolene stenger neste uke",
            List.of(
                new MultipleChoiceContent.Option("a", "Innlegget prøver å få deg til å reagere raskt uten å sjekke"),
                new MultipleChoiceContent.Option("b", "Innlegget handler om skolen"),
                new MultipleChoiceContent.Option("c", "Innlegget har et stort bilde")
            ),
            "a"
        ),
        xOrYTask(
            exercise,
            "Hvilket innlegg virker mest som en faktisk oppklaring?",
            "Riktig. Det ene innlegget virker mer som en vanlig oppklaring, mens det andre prøver å få deg til å reagere raskt.",
            "Se etter hvilket innlegg som virker mest rolig og konkret.",
            "Hvilket innlegg ville du lest først?",
            "Velg innlegget som virker minst stressende.",
            SEED_IMAGE_SOCIAL,
            "Et innlegg som sier at skolene stenger neste uke",
            "",
            "Innlegg 1",
            "Et innlegg om at skolene skal stenge neste uke.",
            SEED_IMAGE_SOCIAL_FACT,
            "Et innlegg som forklarer hva som faktisk gjelder for skolene",
            "",
            "Innlegg 2",
            "Et innlegg om hva som faktisk gjelder for skolene.",
            XOrYContent.Choice.Y
        )
    )));

    return exerciseRepository.save(exercise);
  }

  private Exercise upsertExercise7a() {
    Exercise exercise = exerciseRepository.findByPublicId(SEED_EXERCISE_7A).orElseGet(Exercise::new);
    exercise.setPublicId(SEED_EXERCISE_7A);
    exercise.setTitle("Ukens mysterium: skolekonto");
    exercise.setDifficultyName("Middels");
    exercise.setDescription("Sammenlign meldinger om skolekontoen og finn ut hva som gjør dem trygge eller mistenkelige.");
    exercise.setHint("Se etter avsender, press og om meldingen prøver å få deg til å handle fort.");
    exercise.setNotepadEntry(defaultNotepadEntry("Ukens mysterium: skolekonto",
        "Ikke stol på en melding bare fordi den ser skoleaktig ut."));

    if (exercise.getInternalId() != null) {
      taskRepository.deleteByExerciseId(exercise.getInternalId());
    }

    exercise.getTasks().clear();
    exercise.getTasks().addAll(new HashSet<>(List.of(
        xOrYTask(
            exercise,
            "Hvilken melding virker mest som en vanlig skolebeskjed?",
            "Bra. Den ene meldingen prøver å stresse deg, mens den andre ligner mer på en vanlig beskjed.",
            "Se etter hvilken melding som virker mest rolig og vanlig.",
            "Hvilken melding ville du åpnet først?",
            "Velg den som virker minst mistenkelig.",
            SEED_IMAGE_EMAIL,
            "En melding som sier at konto må oppdateres nå",
            "",
            "Melding 1",
            "En melding om at skolekontoen må oppdateres raskt.",
            SEED_IMAGE_EMAIL_SAFE,
            "En vanlig melding om neste ukes timeplan",
            "",
            "Melding 2",
            "En vanlig beskjed om neste ukes timeplan.",
            XOrYContent.Choice.Y
        ),
        multipleChoiceTask(
            exercise,
            "Hva er det tydeligste faresignalet i meldingen om skolekontoen?",
            "Bra. Når en melding vil at du skal handle raskt og ikke virker helt vanlig, bør du være ekstra forsiktig.",
            "Se etter det som prøver å få deg til å klikke eller svare fort.",
            "Hva skurrer mest?",
            "Velg det sterkeste faresignalet i meldingen.",
            SEED_IMAGE_EMAIL,
            "En melding om at skolekontoen må oppdateres nå",
            List.of(
                new MultipleChoiceContent.Option("a", "Meldingen prøver å få deg til å handle raskt"),
                new MultipleChoiceContent.Option("b", "Meldingen handler om skolekontoen"),
                new MultipleChoiceContent.Option("c", "Meldingen har tekst og bilde")
            ),
            "a"
        )
    )));

    return exerciseRepository.save(exercise);
  }

  private NotepadEntry defaultNotepadEntry(String title, String content) {
    NotepadEntry notepadEntry = new NotepadEntry();
    notepadEntry.setTitle(title);
    notepadEntry.setSource(title);
    notepadEntry.setType(NotepadEntryType.TIP);
    notepadEntry.setContent(content);
    return notepadEntry;
  }

  private Task multipleChoiceTask(Exercise exercise, String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText, String imageUrl,
      String imageAlt, List<MultipleChoiceContent.Option> options, String correctChoice) {
    Task task = new Task();
    task.setExercise(exercise);
    task.setIntroText(introText);
    task.setFeedbackCorrect(feedbackCorrect);
    task.setFeedbackIncorrect(feedbackIncorrect);
    task.setContent(new MultipleChoiceContent(
        new MultipleChoiceContent.Prompt(imageUrl, imageAlt, promptTitle, promptText),
        options,
        correctChoice
    ));
    return task;
  }

  private Task xOrYTask(Exercise exercise, String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText,
      String xImageUrl, String xImageAlt, String xEyebrow, String xTitle, String xText,
      String yImageUrl, String yImageAlt, String yEyebrow, String yTitle, String yText,
      XOrYContent.Choice correctChoice) {
    Task task = new Task();
    task.setExercise(exercise);
    task.setIntroText(introText);
    task.setFeedbackCorrect(feedbackCorrect);
    task.setFeedbackIncorrect(feedbackIncorrect);
    task.setContent(new XOrYContent(
        new XOrYContent.Prompt(promptTitle, promptText),
        new XOrYContent.Options(
            new XOrYContent.Option(xImageUrl, xImageAlt, xEyebrow, xTitle, xText),
            new XOrYContent.Option(yImageUrl, yImageAlt, yEyebrow, yTitle, yText)
        ),
        correctChoice
    ));
    return task;
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

  private String seedSubmissionImage(String objectKey, String classpathResource) {
    mediaStorageService.uploadPublicClasspathImage(objectKey, classpathResource, contentTypeFor(objectKey));
    return objectKey;
  }

  private String contentTypeFor(String path) {
    String normalizedPath = path.toLowerCase(Locale.ROOT);
    if (normalizedPath.endsWith(".png")) {
      return "image/png";
    }

    if (normalizedPath.endsWith(".jpg") || normalizedPath.endsWith(".jpeg")) {
      return "image/jpeg";
    }

    throw new IllegalStateException("Unsupported weekly mystery media type: " + path);
  }

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
