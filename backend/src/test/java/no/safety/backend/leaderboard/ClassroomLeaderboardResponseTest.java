package no.safety.backend.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.safety.backend.leaderboard.dto.ClassroomLeaderboardResponse;

class ClassroomLeaderboardResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeLeaderboardResponseFields() throws Exception {
        ClassroomLeaderboardResponse response = new ClassroomLeaderboardResponse(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Klasse 7B",
                120L,
                2L);

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"classroomPublicUuid\":\"11111111-1111-1111-1111-111111111111\"");
        assertThat(json).contains("\"className\":\"Klasse 7B\"");
        assertThat(json).contains("\"score\":120");
        assertThat(json).contains("\"rank\":2");
    }
}
