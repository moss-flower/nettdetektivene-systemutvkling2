package no.safety.backend.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.auth.dto.TokenPairResponse;
import no.safety.backend.auth.service.AuthenticationService;
import no.safety.backend.auth.service.JwtCookieService;
import no.safety.backend.exception.AccessDeniedException;

/**
 * A controller that provides API endpoints for login, registration and authentication requests.
 * Passes login and registration requests to the service layer.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtCookieService jwtCookieService;

    @Value("${application.security.jwt.cookie.refresh.name}")
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

    /**
     * API endpoint for refresh tokens.
     * Sends an access and refresh token pair in a cookie.
     * @return
     */
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

    /**
     * API endpoint for user logout.
     * Sends an empty cookie to clear the existing cookie in browser.
     * @return
     */
    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout(){
        ResponseCookie accessCookie = jwtCookieService.createEmptyAccessCookie();
        ResponseCookie refreshCookie = jwtCookieService.createEmptyRefreshCookie();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
            .build();
    }

    /**
     * API endpoint to retrieve user details of the logged in user.
     * @param userPrincipal
     * @return
     */
    @GetMapping(value = "/me")
    public ResponseEntity<UserLoginResponse> getActiveUserDetails(@AuthenticationPrincipal UserPrincipal userPrincipal){
        if (userPrincipal == null) {
            throw new AccessDeniedException("No active session found.");
        }
        
        return ResponseEntity.ok(UserLoginResponse.fromEntity(userPrincipal.user(), "User details of logged in user."));
    }

    /**
     * Extracts the cookie value from a request.
     * @param request
     * @param cookieName
     * @return
     */
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
