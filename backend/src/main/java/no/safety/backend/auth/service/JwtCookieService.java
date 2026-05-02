package no.safety.backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service class for creating JWT cookies for both access and refresh tokens.
 * Configures cookie properties such as HttpOnly, Secure, and SameSite policies.
 */
@Service
public class JwtCookieService {

    @Value("${application.security.jwt.cookie.access.name}")
    private String accessCookieName;

    @Value("${application.security.jwt.cookie.access.durationMillis}")
    private long accessCookieDurationMillis;

    @Value("${application.security.jwt.cookie.refresh.name}")
    private String refreshCookieName;

    @Value("${application.security.jwt.cookie.refresh.durationMillis}")
    private long refreshCookieDurationMillis;

    /**
     * Creates an access token cookie with the specified JWT token.
     * @param jwtToken the JWT access token to store in the cookie
     * @return a ResponseCookie configured for access token storage
     */
    public ResponseCookie createAccessCookie(String jwtToken) {
        return ResponseCookie.from(accessCookieName, jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessCookieDurationMillis / 1000)
                .sameSite("Strict")
                .build();
    }

    /**
     * Creates a refresh token cookie with the specified JWT token.
     * @param jwtToken the JWT refresh token to store in the cookie
     * @return a ResponseCookie configured for refresh token storage
     */
    public ResponseCookie createRefreshCookie(String jwtToken) {
        return ResponseCookie.from(refreshCookieName, jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshCookieDurationMillis / 1000)
                .sameSite("Strict")
                .build();
    }

    /**
     * Creates an empty access token cookie to effectively clear the cookie from the browser.
     * Sets maxAge to 0 to expire the cookie immediately.
     * @return an empty ResponseCookie with zero maxAge
     */
    public ResponseCookie createEmptyAccessCookie() {
        return ResponseCookie.from(accessCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)          
                .sameSite("Strict")
                .build();
    }

    /**
     * Creates an empty refresh token cookie to effectively clear the cookie from the browser.
     * Sets maxAge to 0 to expire the cookie immediately.
     * @return an empty ResponseCookie with zero maxAge
     */
    public ResponseCookie createEmptyRefreshCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
