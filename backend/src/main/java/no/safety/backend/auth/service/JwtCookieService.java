package no.safety.backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class JwtCookieService {

    @Value("${application.security.jwt.cookie.name}")
    private String cookieName;

    @Value("${application.security.jwt.cookie.durationMillis}")
    private long cookieDurationMillis;

    public ResponseCookie createCookie(String jwtToken) {
        return ResponseCookie.from(cookieName, jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(cookieDurationMillis / 1000)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createEmptyCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)          
                .sameSite("Strict")
                .build();
    }
}
