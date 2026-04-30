package no.safety.backend.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.auth.dto.UserLoginTokenResponse;
import no.safety.backend.auth.dto.TokenPairResponse;
import no.safety.backend.auth.service.AuthenticationService;
import no.safety.backend.auth.service.JwtCookieService;
import no.safety.backend.registration.service.UserRegistrationService;

/**
 * A controller that provides API endpoints for login, registration and authentication requests.
 * Passes login and registration requests to the service layer.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRegistrationService userRegistrationService;
    private final JwtCookieService jwtCookieService;

    @org.springframework.beans.factory.annotation.Value("${application.security.jwt.cookie.refresh.name}")
    private String refreshCookieName;

    /**
     * API endpoint for user login
     * @param request
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginTokenResponse userTokenResponse = authenticationService.authenticateLogin(request);

        ResponseCookie accessCookie = jwtCookieService.createAccessCookie(userTokenResponse.accessToken());
        ResponseCookie refreshCookie = jwtCookieService.createRefreshCookie(userTokenResponse.refreshToken());
        
        
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
            .body(userTokenResponse.userLoginResponse());
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request){
        String refreshToken = extractCookieValue(request, refreshCookieName);
        TokenPairResponse refreshed = authenticationService.refreshSession(refreshToken);

        ResponseCookie accessCookie = jwtCookieService.createAccessCookie(refreshed.accessToken());
        ResponseCookie refreshCookie = jwtCookieService.createRefreshCookie(refreshed.refreshToken());

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
            .build();
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout(){
        ResponseCookie accessCookie = jwtCookieService.createEmptyAccessCookie();
        ResponseCookie refreshCookie = jwtCookieService.createEmptyRefreshCookie();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
            .build();
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
