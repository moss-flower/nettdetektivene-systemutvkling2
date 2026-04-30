package no.safety.backend.weeklymystery;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.transaction.Transactional;
import no.safety.backend.BaseIntegrationTest;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.TeacherUserRepository;
import no.safety.backend.user.repository.UserRepository;
import no.safety.backend.weeklymystery.repository.WeeklyMysteryPromptRepository;
import no.safety.backend.weeklymystery.repository.WeeklyMysterySubmissionRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
public class WeeklyMysteryIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.cookie.name}")
    private String jwtCookieName;

    @Autowired WeeklyMysteryPromptRepository promptRepository;
    @Autowired WeeklyMysterySubmissionRepository submissionRepository;
    @Autowired TeacherUserRepository teacherUserRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired ClassroomRepository classroomRepository;

    private Classroom classroom;
    private TeacherUser user;

    private WebTestClient webClient;

    protected String jwtCookie;
    protected String xsrfToken;

    protected void loginAs(String email, String password) {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        var result = webClient.post()
        .uri("/api/v1/auth/login")
        .bodyValue(userLoginRequest)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Void.class);

        jwtCookie = result.getResponseCookies().getFirst(jwtCookieName).getValue();
    }
    

    @BeforeEach
    @Transactional
    void setUp() {
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        Role teacherRole = new Role();
        if (roleRepository.findByType(RoleType.ROLE_TEACHER).isEmpty()) {
            teacherRole.setType(RoleType.ROLE_TEACHER);
            roleRepository.save(teacherRole);
        } else {
            teacherRole = roleRepository.findByType(RoleType.ROLE_TEACHER).get();
        }
        System.out.println("Role exists: " + roleRepository.findByType(RoleType.ROLE_TEACHER));
        System.out.println("All roles: " + roleRepository.findAll());

        // 1. Save classroom first (no owner reference yet)
        classroom = new Classroom();
        classroom.setCode("bear-chicken-cow");
        classroom.setDescription("a");
        classroom.setName("wicked");
        classroom.setWorldUid(null);
        classroomRepository.save(classroom);

        // 2. Save user with the already-persisted classroom
        user = new TeacherUser();
        user.setUsername("Jane");
        user.setEmail("jane@test.com");
        user.setPassword(passwordEncoder.encode("password12345"));
        user.setRoles(Set.of(teacherRole));
        user.setClassrooms(Set.of(classroom));
        teacherUserRepository.save(user);

        // 3. Now set the owner back on the classroom and update it
        classroom.setOwners(Set.of(user));
        classroomRepository.save(classroom);

        loginAs("jane@test.com", "password12345");
    }

    // @Test
    // void shouldSubmitNewPromptSuccessfully() {
    //     WeeklyMysteryPromptCreateRequest request = new WeeklyMysteryPromptCreateRequest("title", "prompt", "clue", LocalDate.of(2000,1,1), Set.of(classroom.getUid()));
        
    //     webClient.post()
    //     .uri("/api/v1/WeeklyMystery/prompts")
    //     .cookie(jwtCookieName, jwtCookie)
    //     .bodyValue(request)
    //     .exchange()
    //     .expectStatus().isOk()
    //     .expectBody(WeeklyMysteryPromptResponse.class)
    //     .value(response -> {
    //         assertThat(response.title()).isEqualTo("title");
    //     });
    // }
}
