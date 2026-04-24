package no.safety.backend.gameplay;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import no.safety.backend.BaseIntegrationTest;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.configs.GameplayMockDataInitializer;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.station.dto.StationRequest;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.repository.UserRepository;
import no.safety.backend.world.dto.WorldRequest;
import no.safety.backend.world.dto.WorldResponse;
import no.safety.backend.world.repository.WorldRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@Import(GameplayCrudIntegrationTest.TestSecurityConfig.class)
class GameplayCrudIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private TeacherUserRepository teacherRepo;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private StationRepository stationRepository;

  @Autowired
  private WorldRepository worldRepository;

  @BeforeEach
  void setup() {
    teacherRepo.deleteAll();
  }

  @Test
  void createStation_asTeacher_returnsCreated() {
    TeacherUser teacher = saveTeacher("station-teacher");
    StationRequest request = new StationRequest(
        "Teststasjon",
        "Beskrivelse av teststasjonen.",
        "https://example.com/station.png",
        "#abcdef",
        List.of(
            GameplayMockDataInitializer.NEWSSTAND_EXERCISE_1_UID,
            GameplayMockDataInitializer.POSTOFFICE_EXERCISE_1_UID
        )
    );

    client.post().uri("/api/v1/stations")
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(StationResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.title()).isEqualTo(request.title());
          assertThat(response.description()).isEqualTo(request.description());
          assertThat(response.image()).isEqualTo(request.image());
          assertThat(response.backgroundColor()).isEqualTo(request.backgroundColor());
          assertThat(response.exerciseUids()).containsExactlyElementsOf(request.exerciseUids());
        });
  }

  @Test
  void updateStation_asTeacher_updatesSuccessfully() {
    TeacherUser teacher = saveTeacher("station-update-teacher");
    UUID stationUid = createStation(teacher).uid();
    StationRequest request = new StationRequest(
        "Oppdatert stasjon",
        "Ny beskrivelse.",
        "https://example.com/updated-station.png",
        "#123456",
        List.of(GameplayMockDataInitializer.PHOTOGRAPH_EXERCISE_1_UID)
    );

    client.put().uri("/api/v1/stations/{uid}", stationUid)
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StationResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.uid()).isEqualTo(stationUid);
          assertThat(response.title()).isEqualTo(request.title());
          assertThat(response.exerciseUids()).containsExactlyElementsOf(request.exerciseUids());
        });
  }

  @Test
  void deleteStation_asTeacher_removesStation() {
    TeacherUser teacher = saveTeacher("station-delete-teacher");
    StationResponse created = createStation(teacher);

    client.delete().uri("/api/v1/stations/{uid}", created.uid())
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNoContent();

    assertThat(stationRepository.findByUid(created.uid())).isEmpty();
  }

  @Test
  void createWorld_asTeacher_returnsCreated() {
    TeacherUser teacher = saveTeacher("world-teacher");
    WorldRequest request = new WorldRequest(
        "Testverden",
        "Beskrivelse av testverdenen.",
        "https://example.com/world.png",
        List.of(
            GameplayMockDataInitializer.NEWSSTAND_STATION_UID,
            GameplayMockDataInitializer.POSTOFFICE_STATION_UID
        )
    );

    client.post().uri("/api/v1/worlds")
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(WorldResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.title()).isEqualTo(request.title());
          assertThat(response.description()).isEqualTo(request.description());
          assertThat(response.image()).isEqualTo(request.image());
          assertThat(response.stationUids()).containsExactlyElementsOf(request.stationUids());
        });
  }

  @Test
  void updateWorld_asTeacher_updatesSuccessfully() {
    TeacherUser teacher = saveTeacher("world-update-teacher");
    UUID worldUid = createWorld(teacher).uid();
    WorldRequest request = new WorldRequest(
        "Oppdatert verden",
        "Ny verdensbeskrivelse.",
        "https://example.com/updated-world.png",
        List.of(GameplayMockDataInitializer.MARKETPLACE_STATION_UID)
    );

    client.put().uri("/api/v1/worlds/{uid}", worldUid)
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(WorldResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.uid()).isEqualTo(worldUid);
          assertThat(response.title()).isEqualTo(request.title());
          assertThat(response.stationUids()).containsExactlyElementsOf(request.stationUids());
        });
  }

  @Test
  void deleteWorld_asTeacher_removesWorld() {
    TeacherUser teacher = saveTeacher("world-delete-teacher");
    WorldResponse created = createWorld(teacher);

    client.delete().uri("/api/v1/worlds/{uid}", created.uid())
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .exchange()
        .expectStatus().isNoContent();

    assertThat(worldRepository.findByUid(created.uid())).isEmpty();
  }

  private TeacherUser saveTeacher(String name) {
    var teacherRole = roleRepository.findByType(RoleType.ROLE_TEACHER)
        .orElseThrow(() -> new IllegalStateException("ROLE_TEACHER not found in test database"));

    TeacherUser teacher = teacherRepo.save(TeacherUser.builder()
        .username(name)
        .email(name + "@test.com")
        .password("password")
        .build());

    teacher.setRoles(Set.of(teacherRole));
    return teacherRepo.save(teacher);
  }

  private StationResponse createStation(TeacherUser teacher) {
    StationRequest request = new StationRequest(
        "Startstasjon",
        "En stasjon som brukes i test.",
        "https://example.com/start-station.png",
        "#cfe9ff",
        List.of(GameplayMockDataInitializer.NEWSSTAND_EXERCISE_1_UID)
    );

    return client.post().uri("/api/v1/stations")
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(StationResponse.class)
        .returnResult()
        .getResponseBody();
  }

  private WorldResponse createWorld(TeacherUser teacher) {
    WorldRequest request = new WorldRequest(
        "Startverden",
        "En verden som brukes i test.",
        "https://example.com/start-world.png",
        List.of(GameplayMockDataInitializer.NEWSSTAND_STATION_UID)
    );

    return client.post().uri("/api/v1/worlds")
        .header("X-Test-User", teacher.getPublicUuid().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(WorldResponse.class)
        .returnResult()
        .getResponseBody();
  }

  @TestConfiguration
  static class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        UserRepository userRepository,
        RoleRepository roleRepository) throws Exception {
      http
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .addFilterBefore(new FakeAuthenticationFilter(userRepository, roleRepository),
              UsernamePasswordAuthenticationFilter.class);

      return http.build();
    }
  }

  static class FakeAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    FakeAuthenticationFilter(UserRepository userRepository, RoleRepository roleRepository) {
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
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
          User user = maybeUser.get();

          if (user instanceof TeacherUser) {
            user.setRoles(Set.of(roleRepository.findByType(RoleType.ROLE_TEACHER)
                .orElseThrow(() -> new IllegalStateException("ROLE_TEACHER not found in test database"))));
          }

          UserPrincipal principal = UserPrincipal.fromEntity(user);
          Authentication auth = new UsernamePasswordAuthenticationToken(
              principal, null, principal.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }

      filterChain.doFilter(request, response);
    }
  }
}
