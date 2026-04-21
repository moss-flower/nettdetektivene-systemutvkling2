package no.safety.backend.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.auth.dto.UserLoginTokenResponse;
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

    /**
     * API endpoint for user login
     * @param request
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginTokenResponse userTokenResponse = authenticationService.authenticateLogin(request);

        ResponseCookie jwtCookie = jwtCookieService.createCookie(userTokenResponse.jwtToken());
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userTokenResponse.userLoginResponse());
    }
}
