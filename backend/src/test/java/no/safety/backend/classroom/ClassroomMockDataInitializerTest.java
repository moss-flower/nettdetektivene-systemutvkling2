package no.safety.backend.classroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.completedexercise.repository.CompletedExerciseRepository;
import no.safety.backend.configs.ClassroomMockDataInitializer;
import no.safety.backend.configs.GameplayMockDataInitializer;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ClassroomMockDataInitializerTest {

  @Mock
  private TeacherUserRepository teacherRepository;

  @Mock
  private StudentUserRepository studentRepository;

  @Mock
  private ClassroomRepository classroomRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private CompletedExerciseRepository completedExerciseRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private ClassroomMockDataInitializer initializer;

  @Test
  void run_seedsDemoUsersAndClassrooms() throws Exception {
    when(roleRepository.findByType(RoleType.ROLE_TEACHER)).thenReturn(Optional.of(role(RoleType.ROLE_TEACHER)));
    when(roleRepository.findByType(RoleType.ROLE_STUDENT)).thenReturn(Optional.of(role(RoleType.ROLE_STUDENT)));

    when(teacherRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(classroomRepository.findByUid(any())).thenReturn(Optional.empty());

    when(passwordEncoder.encode(ClassroomMockDataInitializer.DEMO_PASSWORD)).thenReturn("encoded-demo-password");
    when(teacherRepository.save(any(TeacherUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(studentRepository.save(any(StudentUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(classroomRepository.save(any(Classroom.class))).thenAnswer(invocation -> invocation.getArgument(0));

    initializer.run(new DefaultApplicationArguments(new String[0]));

    ArgumentCaptor<Classroom> classroomCaptor = ArgumentCaptor.forClass(Classroom.class);
    ArgumentCaptor<StudentUser> studentCaptor = ArgumentCaptor.forClass(StudentUser.class);
    ArgumentCaptor<TeacherUser> teacherCaptor = ArgumentCaptor.forClass(TeacherUser.class);

    verify(classroomRepository, times(6)).save(classroomCaptor.capture());
    verify(teacherRepository, times(2)).save(teacherCaptor.capture());
    verify(studentRepository, atLeast(6)).save(studentCaptor.capture());

    List<Classroom> savedClassrooms = classroomCaptor.getAllValues();
    List<TeacherUser> savedTeachers = teacherCaptor.getAllValues();
    List<StudentUser> savedStudents = studentCaptor.getAllValues();

    Classroom klasse3a = savedClassrooms.stream()
        .filter(classroom -> classroom.getUid().equals(ClassroomMockDataInitializer.KLASSE_3A_UID))
        .findFirst()
        .orElseThrow();
    Classroom klasse7c = savedClassrooms.stream()
        .filter(classroom -> classroom.getUid().equals(ClassroomMockDataInitializer.KLASSE_7C_UID))
        .findFirst()
        .orElseThrow();
    TeacherUser lisaLaerer = savedTeachers.stream()
        .filter(teacher -> teacher.getEmail().equals("lisa.laerer@test.com"))
        .findFirst()
        .orElseThrow();
    StudentUser adaElev = lastSavedStudent(savedStudents, "ada.elev@test.com");
    StudentUser emilElev = lastSavedStudent(savedStudents, "emil.elev@test.com");

    assertThat(klasse3a.getCode()).isEqualTo(ClassroomMockDataInitializer.KLASSE_3A_CODE);
    assertThat(klasse3a.getName()).isEqualTo("3-trinn A");
    assertThat(klasse3a.getWorldUid()).isEqualTo(GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID);
    assertThat(klasse3a.getOwners()).extracting(user -> user.getUsername()).containsExactly("Lisa Lærer");

    assertThat(klasse7c.getCode()).isEqualTo(ClassroomMockDataInitializer.KLASSE_7C_CODE);
    assertThat(klasse7c.getWorldUid()).isEqualTo(GameplayMockDataInitializer.QUICKSTART_WORLD_UID);
    assertThat(klasse7c.getOwners())
        .extracting(user -> user.getUsername())
        .containsExactlyInAnyOrder("Lisa Lærer", "Per Lærer");

    assertThat(lisaLaerer.getPublicUuid()).isEqualTo(ClassroomMockDataInitializer.LISA_LAERER_UID);
    assertThat(lisaLaerer.getPassword()).isEqualTo("encoded-demo-password");
    assertThat(lisaLaerer.getRoles()).extracting(role -> role.getType()).containsExactly(RoleType.ROLE_TEACHER);

    assertThat(adaElev.getClassroom()).isNotNull();
    assertThat(adaElev.getClassroom().getUid()).isEqualTo(ClassroomMockDataInitializer.KLASSE_3A_UID);
    assertThat(emilElev.getClassroom()).isNotNull();
    assertThat(emilElev.getClassroom().getUid()).isEqualTo(ClassroomMockDataInitializer.KLASSE_7A_UID);
    assertThat(emilElev.getRoles()).extracting(role -> role.getType()).containsExactly(RoleType.ROLE_STUDENT);
  }

  private static Role role(RoleType roleType) {
    return new Role(null, roleType);
  }

  private static StudentUser lastSavedStudent(List<StudentUser> students, String email) {
    StudentUser lastMatch = null;

    for (StudentUser student : students) {
      if (email.equals(student.getEmail())) {
        lastMatch = student;
      }
    }

    if (lastMatch == null) {
      throw new IllegalStateException("Could not find saved student for email: " + email);
    }

    return lastMatch;
  }
}
