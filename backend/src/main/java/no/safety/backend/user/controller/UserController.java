package no.safety.backend.user.controller;

import java.util.UUID;

import no.safety.backend.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.user.dto.UsernameUpdateResponse;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.user.dto.PasswordUpdateRequest;
import no.safety.backend.user.dto.PasswordUpdateResponse;
import no.safety.backend.user.dto.UsernameUpdateRequest;
import no.safety.backend.user.service.UserService;

/**
 * A REST Controller that defines {@link no.safety.backend.user.model.User} API endpoints for users
 * to manage their own account.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Account management")
public class UserController {

  private final UserService userService;

  /**
   * Endpoint for changing the username of a {@link StudentUser}. Can only be used by a teacher
   * account.
   *
   * @param uid     {@link UUID} of a {@link StudentUser}
   * @param request {@link UsernameUpdateRequest}
   * @return a code 200 OK {@link ResponseEntity} containing a {@link UsernameUpdateResponse}
   */
  @PatchMapping("/{uid}/username")
  @PreAuthorize("hasRole('TEACHER')")
  @Operation(summary = "Change a students username. (Requires teacher account)", description = "Endpoint for changing the username of a student. Must be called from a teacher account.")
  public ResponseEntity<UsernameUpdateResponse> updateStudentUsername(
      @PathVariable UUID uid,
      @Valid @RequestBody UsernameUpdateRequest request) {

    return ResponseEntity.ok(userService.updateStudentUsername(uid, request.username()));
  }

  /**
   * Endpoint for changing the username of a {@link User}. Can be used by both teachers and
   * students.
   *
   * @param userPrincipal Spring Security principal for the calling user.
   * @param request {@link UsernameUpdateRequest}
   * @return a code 200 OK {@link ResponseEntity} containing a {@link UsernameUpdateResponse}
   */
  @PatchMapping("/username")
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  @Operation(summary = "Change my username", description = "Endpoint for changing the username of the logged in user.")
  public ResponseEntity<UsernameUpdateResponse> updateUsername(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Valid @RequestBody UsernameUpdateRequest request) {

    return ResponseEntity.ok(
        userService.updateUsername(userPrincipal.getUserInternalId(), request.username()));
  }

  /**
   * Endpoint for changing the password of a {@link User} Can be used by both teachers and
   * students.
   *
   * @param userPrincipal Spring Security principal for the calling user.
   * @param request {@link PasswordUpdateRequest}
   * @returna code 200 OK {@link ResponseEntity} containing a {@link PasswordUpdateResponse}
   */
  @PatchMapping("/password")
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  @Operation(summary = "Change my password", description = "Endpoint for changing the password of the logged in user.")
  public ResponseEntity<PasswordUpdateResponse> updatePassword(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Valid @RequestBody PasswordUpdateRequest request) {

    return ResponseEntity.ok(
        userService.updatePassword(userPrincipal.getUserInternalId(), request.password()));
  }
}
