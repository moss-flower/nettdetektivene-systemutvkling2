package no.safety.backend.classroom;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.repository.UserRepository;
import no.safety.backend.auth.model.UserPrincipal;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.filter.OncePerRequestFilter;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class ClassroomManagementIntegrationTest {

  private static final UUID DEFAULT_WORLD_UID = no.safety.backend.configs.GameplayMockDataInitializer.SCHOOLYARD_WORLD_UID;
  private static final UUID QUICKSTART_WORLD_UID = no.safety.backend.configs.GameplayMockDataInitializer.QUICKSTART_WORLD_UID;

  @Container
  @ServiceConnection
  static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

  @Autowired
  private RestTestClient client;

  @Autowired
  private ClassroomRepository classroomRepository;

  @Autowired
  private TeacherUserRepository teacherRepo;

  @Autowired
  private StudentUserRepository studentRepo;

  @BeforeEach
  void setup() {
    studentRepo.deleteAll();
    classroomRepository.deleteAll();
    teacherRepo.deleteAll();
  }

  @Test
  void create_asTeacher_returnsCreated() {
    TeacherUser teacher = saveTeacher("teacher1");

    client.post().uri("/api/v1/classrooms")
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Klasse 5B", "Har mange tullinger", DEFAULT_WORLD_UID))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(ClassroomResponse.class)
        .value(r -> {
          assert r != null;
          assertThat(r.name()).isEqualTo("Klasse 5B");
          assertThat(r.description()).isEqualTo("Har mange tullinger");
          assertThat(r.worldUid()).isEqualTo(DEFAULT_WORLD_UID);
          assertThat(r.code()).isNotBlank();
          assertThat(r.owners()).hasSize(1);
          assertThat(r.owners().getFirst().publicUuid()).isEqualTo(teacher.getPublicUuid());
          assertThat(r.members()).isEmpty();
        });
  }

  @Test
  void createClassroom_asStudent_returnsNotFound() {
    StudentUser student = saveStudent("student1");

    client.post().uri("/api/v1/classrooms")
        .header("X-Test-User", student.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Klasse 5B", "Har mange tullinger", DEFAULT_WORLD_UID))
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void createClassroom_withNonexistentUser_returnsNotFound() {
    client.post().uri("/classrooms")
        .header("X-Test-User", UUID.randomUUID().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Klasse 5B", "Har mange tullinger", DEFAULT_WORLD_UID))
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void updateClassroom_asOwner_updatesSuccessfully() {
    TeacherUser teacher = saveTeacher("teacher1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.put().uri("/api/v1/classrooms/{id}", created.uid())
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Klasse 7C", "En seriøs klasse", QUICKSTART_WORLD_UID))
        .exchange()
        .expectStatus().isOk()
        .expectBody(ClassroomResponse.class)
        .value(r -> {
          assertThat(r.name()).isEqualTo("Klasse 7C");
          assertThat(r.description()).isEqualTo("En seriøs klasse");
          assertThat(r.worldUid()).isEqualTo(QUICKSTART_WORLD_UID);
        });
  }

  @Test
  void updateClassroom_asNonOwner_returnsForbidden() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.put().uri("/api/v1/classrooms/{id}", created.uid())
        .header("X-Test-User", other.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Hacked", "Hacked", QUICKSTART_WORLD_UID))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void updateClassroom_nonexistentClassroom_returnsNotFound() {
    TeacherUser teacher = saveTeacher("teacher1");

    client.put().uri("/api/v1/classrooms/{id}", 9999)
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest("Klasse 7C", "En seriøs klasse", QUICKSTART_WORLD_UID))
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void deleteClassroom_asOwner_returnsNoContent() {
    TeacherUser teacher = saveTeacher("teacher1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.delete().uri("/api/v1/classrooms/{id}", created.uid())
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  void deleteClassroom_asNonOwner_returnsForbidden() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.delete().uri("/api/v1/classrooms/{id}", created.uid())
        .header("X-Test-User", other.getPublicUuid().toString())
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void deleteClassroom_nonexistentClassroom_returnsNotFound() {
    TeacherUser teacher = saveTeacher("teacher1");

    client.delete().uri("/api/v1/classrooms/{id}", 9999)
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNotFound();
  }


  @Test
  void addOwner_asOwner_addsSuccessfully() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser newOwner = saveTeacher("teacher2");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/{id}/owners/{uuid}", created.uid(), newOwner.getPublicUuid())
        .header("X-Test-User", owner.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk()
        .expectBody(ClassroomResponse.class)
        .value(r -> assertThat(r.owners()).hasSize(2));
  }

  @Test
  void addOwner_asNonOwner_returnsForbidden() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");
    TeacherUser third = saveTeacher("teacher3");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/{id}/owners/{uuid}", created.uid(), third.getPublicUuid())
        .header("X-Test-User", other.getPublicUuid().toString())
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void joinByCode_asStudent_returnsOk() {
    TeacherUser teacher = saveTeacher("teacher1");
    StudentUser student = saveStudent("student1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", created.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk()
        .expectBody(ClassroomResponse.class)
        .value(r -> {
          assertThat(r.members()).hasSize(1);
          assertThat(r.members().getFirst().publicUuid()).isEqualTo(student.getPublicUuid());
        });
  }

  @Test
  void joinByCode_asTeacher_returnsNotFound() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", created.code())
        .header("X-Test-User", other.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void joinByCode_alreadyInClassroom_returnsConflict() {
    TeacherUser teacher1 = saveTeacher("teacher1");
    TeacherUser teacher2 = saveTeacher("teacher2");
    StudentUser student = saveStudent("student1");
    ClassroomResponse first = createClassroom(teacher1, "Klasse 5B");
    ClassroomResponse second = createClassroom(teacher2, "Science 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", first.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk();

    client.post().uri("/api/v1/classrooms/join/{code}", second.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isEqualTo(409);
  }

  @Test
  void joinByCode_invalidCode_returnsNotFound() {
    StudentUser student = saveStudent("student1");

    client.post().uri("/api/v1/classrooms/join/{code}", "INVALID-CODE")
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void removeMember_asOwner_removesMember() {
    TeacherUser teacher = saveTeacher("teacher1");
    StudentUser student = saveStudent("student1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", created.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk();

    client.delete().uri("/api/v1/classrooms/{id}/members/{uuid}", created.uid(), student.getPublicUuid())
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk()
        .expectBody(ClassroomResponse.class)
        .value(r -> assertThat(r.members()).isEmpty());
  }

  @Test
  void removeMember_asNonOwner_returnsForbidden() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");
    StudentUser student = saveStudent("student1");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", created.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk();

    client.delete().uri("/api/v1/classrooms/{id}/members/{uuid}", created.uid(), student.getPublicUuid())
        .header("X-Test-User", other.getPublicUuid().toString())
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void leaveClassroom_asMember_returnsNoContent() {
    TeacherUser teacher = saveTeacher("teacher1");
    StudentUser student = saveStudent("student1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/join/{code}", created.code())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isOk();

    client.delete().uri("/api/v1/classrooms/{id}/members/me", created.uid())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  void leaveClassroom_notAMember_returnsNotFound() {
    TeacherUser teacher = saveTeacher("teacher1");
    StudentUser student = saveStudent("student1");
    ClassroomResponse created = createClassroom(teacher, "Klasse 5B");

    client.delete().uri("/api/v1/classrooms/{id}/members/me", created.uid())
        .header("X-Test-User", student.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void regenerateCode_asOwner_returnsSuccessful() {
    TeacherUser owner = saveTeacher("teacher1");
    ClassroomResponse created = createClassroom(owner, "Klasse 5B");
    String oldCode = created.code();

    client.post().uri("/api/v1/classrooms/{id}/code", created.uid())
        .header("X-Test-User", owner.getPublicUuid().toString()).exchange().expectStatus()
        .isOk()
        .expectBody(ClassroomResponse.class).value(r -> {
          assert r != null;
          assertThat(!r.code().equals(oldCode));
        });
  }

  @Test
  void regenerateCode_asNonOwner_returnsForbidden() {
    TeacherUser owner = saveTeacher("teacher1");
    TeacherUser other = saveTeacher("teacher2");

    ClassroomResponse created = createClassroom(owner, "Klasse 5B");

    client.post().uri("/api/v1/classrooms/{id}/code", created.uid())
        .header("X-Test-User", other.getPublicUuid().toString()).exchange().expectStatus()
        .isForbidden();
  }

  @Test
  void regenerateCode_whenClassroomNotFound_returnsNotFound() {
    TeacherUser teacher = saveTeacher("teacher1");

    client.post().uri("/api/v1/classrooms/{id}/code", 6767)
        .header("X-Test-User", teacher.getPublicUuid().toString()).exchange().expectStatus()
        .isNotFound();
  }

  private TeacherUser saveTeacher(String name) {
    return teacherRepo.save(TeacherUser.builder()
        .username(name)
        .email(name + "@test.com")
        .password("password")
        .build());
  }

  private StudentUser saveStudent(String name) {
    return studentRepo.save(StudentUser.builder()
        .username(name)
        .email(name + "@test.com")
        .password("password")
        .build());
  }

  private ClassroomResponse createClassroom(TeacherUser owner, String name) {
    return client.post().uri("/api/v1/classrooms")
        .header("X-Test-User", owner.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ClassroomRequest(name, "description", DEFAULT_WORLD_UID))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(ClassroomResponse.class)
        .returnResult().getResponseBody();
  }


  @TestConfiguration
  static class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        UserRepository userRepository) throws Exception {
      http
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .addFilterBefore(new FakeAuthenticationFilter(userRepository),
              UsernamePasswordAuthenticationFilter.class);

      return http.build();
    }
  }

  static class FakeAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    FakeAuthenticationFilter(UserRepository userRepository) {
      this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain)
        throws ServletException, IOException {

      String header = request.getHeader("X-Test-User");

      if (header != null) {
        Optional<User> maybeUser = userRepository.findByPublicUuid(UUID.fromString(header));
        if (maybeUser.isPresent()) {
          UserPrincipal principal = new UserPrincipal(maybeUser.get(), List.of());
          Authentication auth = new UsernamePasswordAuthenticationToken(
              principal, null, principal.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
          SecurityContextHolder.getContext().setAuthentication(
              new UsernamePasswordAuthenticationToken(header, null, List.of()));
        }
      }

      filterChain.doFilter(request, response);
    }
  }
}
