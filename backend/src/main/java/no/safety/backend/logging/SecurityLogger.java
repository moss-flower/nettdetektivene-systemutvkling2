package no.safety.backend.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized security logger that wraps SLF4J to enforce safe logging
 * practices.
 * <p>
 * Sanitizes untrusted input to prevent CRLF and log injection vulnerabilities.
 * Use this class for all security-relevant audit events instead of raw loggers.
 */
public class SecurityLogger {
    private static final Logger logger = LoggerFactory.getLogger(SecurityLogger.class);

    /**
     * Logs a security action after sanitizing the username against CRLF injection.
     * <p>
     * Replaces carriage returns ({@code \r}) and line feeds ({@code \n}) with
     * underscores.
     *
     * @param username the untrusted user identifier to sanitize and log
     * @param action   a descriptive constant for the operation (e.g.,
     *                 "LOGIN_SUCCESS")
     */
    public void logAction(String username, String action) {
        String sanitizedUsername = username.replaceAll("[\r\n]", "_");
        logger.info("User [{}] performed action: {}", sanitizedUsername, action);
    }
}