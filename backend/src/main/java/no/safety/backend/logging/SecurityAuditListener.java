package no.safety.backend.logging;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Intercepts Spring Security authentication events and routes them to the
 * centralized security audit log.
 */
@Component
public class SecurityAuditListener {

    private final SecurityLogger securityLogger;

    /**
     * Constructs the audit listener with the required security logging utility.
     *
     * @param securityLogger the centralized logger responsible for sanitizing
     *                       untrusted input
     */
    public SecurityAuditListener(SecurityLogger securityLogger) {
        this.securityLogger = securityLogger;
    }

    /**
     * Captures and logs successful authentication events.
     *
     * @param event the Spring Security event containing the authenticated user's
     *              principal details
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.logAction(username, "LOGIN_SUCCESS");
    }

    /**
     * Captures and logs failed authentication attempts.
     * <p>
     * Consistently monitoring these events is critical for detecting brute-force
     * attacks, credential stuffing, or misconfigured client applications.
     * </p>
     *
     * @param event the Spring Security event containing the failed authentication
     *              details
     */
    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.logAction(username, "LOGIN_FAILURE");
    }
}