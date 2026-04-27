package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.completedexercise.model.CompletedExercise;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

  public static final UUID KLASSE_5B_UID = stableUuid("729b159a-97a1-4917-a71b-901420a2749d");
  public static final UUID KLASSE_6A_UID = stableUuid("d217f63d-1252-419f-8106-998728797235");
  public static final UUID NETTDETEKTIVENE_UID = stableUuid("43369b40-64f0-4692-83b4-8736581364a6");

  public static final String KLASSE_5B_CODE = "SUPER-SKOG-KOPP";
  public static final String KLASSE_6A_CODE = "MODIG-TAVLE-BRUS";
  public static final String NETTDETEKTIVENE_CODE = "KONGE-LAVA-TRE";

  private final TeacherUserRepository teacherRepository;
  private final StudentUserRepository studentRepository;
  private final ClassroomRepository classroomRepository;
  private final RoleRepository roleRepository;
  private final CompletedExerciseRepository completedExerciseRepository;
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

    clearSeededMemberships(List.of(adaElev, eliasElev, saraElev, noahElev, ingridElev, emilElev, miaElev,
        lukasElev));

    Classroom klasse5b = upsertClassroom(
        KLASSE_5B_UID,
        KLASSE_5B_CODE,
        "Klasse 5B",
        "En nysgjerrig klasse som trener på å kjenne igjen falske nyheter og rare lenker.",
        GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID,
        Set.of(lisaLaerer)
    );
    Classroom klasse6a = upsertClassroom(
        KLASSE_6A_UID,
        KLASSE_6A_CODE,
        "Klasse 6A",
        "En klasse som jobber raskt gjennom korte oppdrag og øver på gode sikkerhetsvaner.",
        GameplayMockDataInitializer.QUICKSTART_WORLD_UID,
        Set.of(perLaerer)
    );
    Classroom nettdetektivene = upsertClassroom(
        NETTDETEKTIVENE_UID,
        NETTDETEKTIVENE_CODE,
        "Nettdetektivene",
        "En liten gruppe som tester bildeanalyse, kildekritikk og nye mysterier sammen.",
        GameplayMockDataInitializer.CREATIVE_LAB_WORLD_UID,
        Set.of(lisaLaerer, perLaerer)
    );

    assignMembers(klasse5b, List.of(adaElev, eliasElev, saraElev, lukasElev));
    assignMembers(klasse6a, List.of(noahElev, ingridElev));
    assignMembers(nettdetektivene, List.of(emilElev));

    markAllExercisesCompleted(lukasElev);
  }

  private void markAllExercisesCompleted(StudentUser student) {
    List<UUID> allExerciseUids = List.of(
        GameplayMockDataInitializer.NEWSSTAND_EXERCISE_1_UID,
        GameplayMockDataInitializer.NEWSSTAND_EXERCISE_2_UID,
        GameplayMockDataInitializer.PHOTOGRAPH_EXERCISE_1_UID,
        GameplayMockDataInitializer.PHOTOGRAPH_EXERCISE_2_UID,
        GameplayMockDataInitializer.POSTOFFICE_EXERCISE_1_UID,
        GameplayMockDataInitializer.POSTOFFICE_EXERCISE_2_UID,
        GameplayMockDataInitializer.PASSWORDBANK_EXERCISE_1_UID,
        GameplayMockDataInitializer.PASSWORDBANK_EXERCISE_2_UID,
        GameplayMockDataInitializer.MARKETPLACE_EXERCISE_1_UID,
        GameplayMockDataInitializer.MARKETPLACE_EXERCISE_2_UID,
        GameplayMockDataInitializer.SOCIALMEDIA_EXERCISE_1_UID,
        GameplayMockDataInitializer.SOCIALMEDIA_EXERCISE_2_UID,
        GameplayMockDataInitializer.DATACENTER_EXERCISE_UID
    );

    for (UUID exerciseUid : allExerciseUids) {
      if (completedExerciseRepository.existsByStudentUidAndExerciseUid(
          student.getPublicUuid(), exerciseUid)) {
        continue;
      }
      CompletedExercise completion = new CompletedExercise();
      completion.setStudentUid(student.getPublicUuid());
      completion.setExerciseUid(exerciseUid);
      completedExerciseRepository.save(completion);
    }
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
    teacher.setRoles(Set.of(teacherRole));

    return teacherRepository.save(teacher);
  }

  private StudentUser upsertStudent(UUID publicUuid, String username, String email, Role studentRole) {
    StudentUser student = studentRepository.findByEmail(email).orElseGet(StudentUser::new);

    student.setPublicUuid(publicUuid);
    student.setUsername(username);
    student.setEmail(email);
    student.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
    student.setRoles(Set.of(studentRole));

    return studentRepository.save(student);
  }

  private void clearSeededMemberships(List<StudentUser> students) {
    for (StudentUser student : students) {
      student.setClassroom(null);
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
      studentRepository.save(member);
    }
  }

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
