package no.safety.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * A base integration test class to make integration testing easier.
 * Extend from this class when writing new integration tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {
    @Container
    @ServiceConnection
    static protected MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");
    
    @Autowired
    protected RestTestClient client;
    
}
