package no.safety.backend.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import no.safety.backend.auth.dto.UserLoginRequest;
import no.safety.backend.auth.dto.UserLoginResponse;
import no.safety.backend.auth.dto.UserLoginTokenResponse;
import no.safety.backend.auth.service.AuthenticationService;
import no.safety.backend.auth.service.JwtCookieService;
import no.safety.backend.registration.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private UserRegistrationService userRegistrationService;

  @Mock
  private JwtCookieService jwtCookieService;

  @InjectMocks
  private AuthenticationController authenticationController;

  @Test
  void login_returnsCookieAndBody() {
    UserLoginRequest request = new UserLoginRequest("user@test.no", "secret_password");
    UserLoginResponse loginResponse = new UserLoginResponse(
        UUID.randomUUID(),
        "user",
        "user@test.no",
        Set.of("ROLE_STUDENT"),
        "Login successful"
    );
    UserLoginTokenResponse tokenResponse = new UserLoginTokenResponse(loginResponse, "jwt-token");
    ResponseCookie cookie = ResponseCookie.from("JWT-Cookie", "jwt-token")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .build();

    when(authenticationService.authenticateLogin(request)).thenReturn(tokenResponse);
    when(jwtCookieService.createCookie("jwt-token")).thenReturn(cookie);

    ResponseEntity<UserLoginResponse> response = authenticationController.login(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(cookie.toString(), response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    assertSame(loginResponse, response.getBody());

    verify(authenticationService).authenticateLogin(request);
    verify(jwtCookieService).createCookie("jwt-token");
  }
}
