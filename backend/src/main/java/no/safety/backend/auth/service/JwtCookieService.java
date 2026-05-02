package no.safety.backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

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

    public ResponseCookie createAccessCookie(String jwtToken) {
        return ResponseCookie.from(accessCookieName, jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessCookieDurationMillis / 1000)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createRefreshCookie(String jwtToken) {
        return ResponseCookie.from(refreshCookieName, jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshCookieDurationMillis / 1000)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createEmptyAccessCookie() {
        return ResponseCookie.from(accessCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)          
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createEmptyRefreshCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createEmptyCsrfCookie() {
        return ResponseCookie.from("XSRF-TOKEN", "")
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
