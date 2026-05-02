package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.notification.model.Notification;
import no.safety.backend.notification.repository.NotificationRepository;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ClassroomMockDataInitializer implements ApplicationRunner {

  public static final String DEMO_PASSWORD = "test123";

  public static final UUID LISA_LAERER_UID = stableUuid("f712fe6a-bc79-42ec-bc7b-6f5e0bda34e9");
  public static final UUID PER_LAERER_UID = stableUuid("9b326835-b645-4134-9687-c39634919714");

  public static final UUID ADA_ELEV_UID = stableUuid("61734d7f-f5a5-464f-a08e-45115b76916d");
  public static final UUID ELIAS_ELEV_UID = stableUuid("b97d4b7f-9130-4194-94a0-434c68166551");
  public static final UUID SARA_ELEV_UID = stableUuid("7b5167a6-418f-40b3-9966-c6c28c1520c3");
  public static final UUID NOAH_ELEV_UID = stableUuid("a14420f5-0361-484c-a693-800330881035");
  public static final UUID INGRID_ELEV_UID = stableUuid("3973814f-8816-4698-925e-40c315472072");
  public static final UUID EMIL_ELEV_UID = stableUuid("87229500-189e-4823-8874-0a6014430d1d");
  public static final UUID MIA_ELEV_UID = stableUuid("54550c30-f4ac-44f0-96e6-fcfe4d9d9bd5");
  public static final UUID LUKAS_ELEV_UID = stableUuid("ae7780a8-f017-4956-9468-d72d3617baa0");
  public static final UUID ODA_NO_CLASSROOM_ELEV_UID = stableUuid("student:oda-no-classroom");
  public static final UUID THEO_PENDING_ELEV_UID = stableUuid("student:theo-pending");
  public static final UUID AMALIE_PENDING_ELEV_UID = stableUuid("student:amalie-pending");
  public static final UUID HENRIK_PENDING_ELEV_UID = stableUuid("student:henrik-pending");
  public static final UUID NORA_PENDING_ELEV_UID = stableUuid("student:nora-pending");
  public static final UUID AKSEL_PENDING_ELEV_UID = stableUuid("student:aksel-pending");
  public static final UUID VILDE_PENDING_ELEV_UID = stableUuid("student:vilde-pending");

  public static final UUID KLASSE_3A_UID = stableUuid("729b159a-97a1-4917-a71b-901420a2749d");
  public static final UUID KLASSE_3B_UID = stableUuid("d217f63d-1252-419f-8106-998728797235");
  public static final UUID KLASSE_3C_UID = stableUuid("43369b40-64f0-4692-83b4-8736581364a6");
  public static final UUID KLASSE_7A_UID = stableUuid("classroom:7-trinn-a");
  public static final UUID KLASSE_7B_UID = stableUuid("classroom:7-trinn-b");
  public static final UUID KLASSE_7C_UID = stableUuid("classroom:7-trinn-c");

  public static final String KLASSE_3A_CODE = "SUPER-SKOG-KOPP";
  public static final String KLASSE_3B_CODE = "MODIG-TAVLE-BRUS";
  public static final String KLASSE_3C_CODE = "KONGE-LAVA-TRE";
  public static final String KLASSE_7A_CODE = "TRYGG-REV-SOL";
  public static final String KLASSE_7B_CODE = "KLAR-FJELL-BOK";
  public static final String KLASSE_7C_CODE = "RASK-ELG-LYS";

  private final TeacherUserRepository teacherRepository;
  private final StudentUserRepository studentRepository;
  private final ClassroomRepository classroomRepository;
  private final RoleRepository roleRepository;
  private final CompletedExerciseRepository completedExerciseRepository;
  private final NotificationRepository notificationRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Role teacherRole = getOrCreateRole(RoleType.ROLE_TEACHER);
    Role studentRole = getOrCreateRole(RoleType.ROLE_STUDENT);

    TeacherUser lisaLaerer = upsertTeacher(
        LISA_LAERER_UID,
        "Lisa Lærer",
        "lisa.laerer@test.com",
        teacherRole
    );
    TeacherUser perLaerer = upsertTeacher(
        PER_LAERER_UID,
        "Per Lærer",
        "per.laerer@test.com",
        teacherRole
    );

    StudentUser adaElev = upsertStudent(ADA_ELEV_UID, "Ada Elev", "ada.elev@test.com", studentRole);
    StudentUser eliasElev = upsertStudent(ELIAS_ELEV_UID, "Elias Elev", "elias.elev@test.com", studentRole);
    StudentUser saraElev = upsertStudent(SARA_ELEV_UID, "Sara Elev", "sara.elev@test.com", studentRole);
    StudentUser noahElev = upsertStudent(NOAH_ELEV_UID, "Noah Elev", "noah.elev@test.com", studentRole);
    StudentUser ingridElev = upsertStudent(INGRID_ELEV_UID, "Ingrid Elev", "ingrid.elev@test.com", studentRole);
    StudentUser emilElev = upsertStudent(EMIL_ELEV_UID, "Emil Elev", "emil.elev@test.com", studentRole);
    StudentUser miaElev = upsertStudent(MIA_ELEV_UID, "Mia Elev", "mia.elev@test.com", studentRole);
    StudentUser lukasElev = upsertStudent(LUKAS_ELEV_UID, "Lukas Elev", "lukas.elev@test.com", studentRole);
    StudentUser odaNoClassroomElev = upsertStudent(ODA_NO_CLASSROOM_ELEV_UID, "Oda Uten Klasse", "oda.utenklasse@test.com", studentRole);
    StudentUser theoPendingElev = upsertStudent(THEO_PENDING_ELEV_UID, "Theo Venter", "theo.venter@test.com", studentRole);
    StudentUser amaliePendingElev = upsertStudent(AMALIE_PENDING_ELEV_UID, "Amalie Venter", "amalie.venter@test.com", studentRole);
    StudentUser henrikPendingElev = upsertStudent(HENRIK_PENDING_ELEV_UID, "Henrik Venter", "henrik.venter@test.com", studentRole);
    StudentUser noraPendingElev = upsertStudent(NORA_PENDING_ELEV_UID, "Nora Venter", "nora.venter@test.com", studentRole);
    StudentUser akselPendingElev = upsertStudent(AKSEL_PENDING_ELEV_UID, "Aksel Venter", "aksel.venter@test.com", studentRole);
    StudentUser vildePendingElev = upsertStudent(VILDE_PENDING_ELEV_UID, "Vilde Venter", "vilde.venter@test.com", studentRole);

    clearSeededMemberships(List.of(adaElev, eliasElev, saraElev, noahElev, ingridElev, emilElev, miaElev,
        lukasElev, odaNoClassroomElev, theoPendingElev, amaliePendingElev, henrikPendingElev, noraPendingElev,
        akselPendingElev, vildePendingElev));

    Classroom klasse3a = upsertClassroom(
        KLASSE_3A_UID,
        KLASSE_3A_CODE,
        "3-trinn A",
        "En nysgjerrig klasse som trener på å kjenne igjen falske nyheter og rare bilder.",
        GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID,
        Set.of(lisaLaerer)
    );
    Classroom klasse3b = upsertClassroom(
        KLASSE_3B_UID,
        KLASSE_3B_CODE,
        "3-trinn B",
        "En klasse som øver på å stoppe opp og sjekke før de tror på en sak.",
        GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID,
        Set.of(perLaerer)
    );
    Classroom klasse3c = upsertClassroom(
        KLASSE_3C_UID,
        KLASSE_3C_CODE,
        "3-trinn C",
        "En liten gruppe som løser bildeoppdrag og kildekritikk sammen.",
        GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID,
        Set.of(lisaLaerer, perLaerer)
    );
    Classroom klasse7a = upsertClassroom(
        KLASSE_7A_UID,
        KLASSE_7A_CODE,
        "7-trinn A",
        "En klasse som jobber med hele nettdetektiv-løypa og mer avanserte valg på nett.",
        GameplayMockDataInitializer.QUICKSTART_WORLD_UID,
        Set.of(lisaLaerer)
    );
    Classroom klasse7b = upsertClassroom(
        KLASSE_7B_UID,
        KLASSE_7B_CODE,
        "7-trinn B",
        "En klasse som øver på e-post, passord, nettbutikker og deling i sosiale medier.",
        GameplayMockDataInitializer.QUICKSTART_WORLD_UID,
        Set.of(perLaerer)
    );
    Classroom klasse7c = upsertClassroom(
        KLASSE_7C_UID,
        KLASSE_7C_CODE,
        "7-trinn C",
        "En samarbeidsklasse for elever som tester hele sikkerhetsløypa.",
        GameplayMockDataInitializer.QUICKSTART_WORLD_UID,
        Set.of(lisaLaerer, perLaerer)
    );
    assignMembers(klasse3a, List.of(adaElev, eliasElev));
    assignMembers(klasse3b, List.of(saraElev));
    assignMembers(klasse3c, List.of(noahElev));
    assignMembers(klasse7a, List.of(ingridElev, emilElev));
    assignMembers(klasse7b, List.of(miaElev));
    assignMembers(klasse7c, List.of(lukasElev));
    assignPendingMember(klasse3a, theoPendingElev);
    assignPendingMember(klasse3b, amaliePendingElev);
    assignPendingMember(klasse3c, henrikPendingElev);
    assignPendingMember(klasse7a, noraPendingElev);
    assignPendingMember(klasse7b, akselPendingElev);
    assignPendingMember(klasse7c, vildePendingElev);

    seedNotifications(lisaLaerer, perLaerer, adaElev, eliasElev, miaElev, lukasElev);
  }

  private Role getOrCreateRole(RoleType roleType) {
    return roleRepository.findByType(roleType)
        .orElseGet(() -> roleRepository.save(new Role(null, roleType)));
  }

  private TeacherUser upsertTeacher(UUID publicUuid, String username, String email, Role teacherRole) {
    TeacherUser teacher = teacherRepository.findByEmail(email).orElseGet(TeacherUser::new);

    teacher.setPublicUuid(publicUuid);
    teacher.setUsername(username);
    teacher.setEmail(email);
    teacher.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
    teacher.setRoles(new HashSet<>(Set.of(teacherRole)));

    return teacherRepository.save(teacher);
  }

  private StudentUser upsertStudent(UUID publicUuid, String username, String email, Role studentRole) {
    StudentUser student = studentRepository.findByEmail(email).orElseGet(StudentUser::new);

    student.setPublicUuid(publicUuid);
    student.setUsername(username);
    student.setEmail(email);
    student.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
    student.setRoles(new HashSet<>(Set.of(studentRole)));

    return studentRepository.save(student);
  }

  private void clearSeededMemberships(List<StudentUser> students) {
    for (StudentUser student : students) {
      student.setClassroom(null);
      student.setClassroomPending(false);
      studentRepository.save(student);
    }
  }

  private Classroom upsertClassroom(UUID uid, String code, String name, String description, UUID worldUid,
      Set<TeacherUser> owners) {
    Classroom classroom = classroomRepository.findByUid(uid).orElseGet(Classroom::new);

    classroom.setUid(uid);
    classroom.setCode(code);
    classroom.setName(name);
    classroom.setDescription(description);
    classroom.setWorldUid(worldUid);
    classroom.setOwners(new HashSet<>(owners));

    return classroomRepository.save(classroom);
  }

  private void assignMembers(Classroom classroom, List<StudentUser> members) {
    for (StudentUser member : members) {
      member.setClassroom(classroom);
      member.setClassroomPending(false);
      studentRepository.save(member);
    }
  }

  private void assignPendingMember(Classroom classroom, StudentUser member) {
    member.setClassroom(classroom);
    member.setClassroomPending(true);
    studentRepository.save(member);
  }

  private void seedNotifications(
      TeacherUser lisaLaerer,
      TeacherUser perLaerer,
      StudentUser adaElev,
      StudentUser eliasElev,
      StudentUser miaElev,
      StudentUser lukasElev) {
    if (lisaLaerer.getInternalId() == null || perLaerer.getInternalId() == null) {
      return;
    }

    notificationRepository.deleteAll(notificationRepository.findByTarget_InternalId(lisaLaerer.getInternalId()));
    notificationRepository.deleteAll(notificationRepository.findByTarget_InternalId(perLaerer.getInternalId()));

    notificationRepository.saveAll(List.of(
        seededNotification(
            lisaLaerer,
            adaElev,
            "Ada Elev fullforte et oppgavesett og ber om at du ser gjennom besvarelsen.",
            3,
            LocalDateTime.now().minusMinutes(18)),
        seededNotification(
            lisaLaerer,
            eliasElev,
            "Elias Elev trenger hjelp i 3-trinn A etter flere feil pa rad.",
            2,
            LocalDateTime.now().minusMinutes(9)),
        seededNotification(
            perLaerer,
            miaElev,
            "Mia Elev sendte inn et sporsmal om nettbutikk-oppgaven.",
            2,
            LocalDateTime.now().minusMinutes(14)),
        seededNotification(
            perLaerer,
            lukasElev,
            "Lukas Elev fullforte dagens stasjonskart i 7-trinn C.",
            1,
            LocalDateTime.now().minusMinutes(4))));
  }

  private Notification seededNotification(
      TeacherUser target,
      StudentUser sender,
      String message,
      int priority,
      LocalDateTime createdAt) {
    return Notification.builder()
        .message(message)
        .priority(priority)
        .createdAt(createdAt)
        .publicId(UUID.randomUUID())
        .senderName(sender.getUsername())
        .senderId(sender.getPublicUuid())
        .target(target)
        .build();
  }

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
