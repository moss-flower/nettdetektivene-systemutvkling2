package no.safety.backend.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for the Argon2 password hashing algorithm.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.security.crypto.argon2")
public class Argon2ConfigurationProperties {
    private int memoryUsageKilobytes;
    private int parallelism;
    private int iterations;
    private int saltLength;
    private int hashLength;
}
