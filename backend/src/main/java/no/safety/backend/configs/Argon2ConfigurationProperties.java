package no.safety.backend.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for the Argon2 password hashing algorithm.
 * Maps properties with prefix "application.security.crypto.argon2".
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.security.crypto.argon2")
public class Argon2ConfigurationProperties {
    /**
     * Memory usage in kilobytes for Argon2 hashing.
     */
    private int memoryUsageKilobytes;

    /**
     * Parallelism factor for Argon2 hashing.
     */
    private int parallelism;

    /**
     * Number of iterations for Argon2 hashing.
     */
    private int iterations;

    /**
     * Length of the salt in bytes for Argon2 hashing.
     */
    private int saltLength;

    /**
     * Length of the hash output in bytes for Argon2 hashing.
     */
    private int hashLength;
}
