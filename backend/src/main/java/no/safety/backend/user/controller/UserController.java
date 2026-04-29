package no.safety.backend.user.controller;

import java.util.UUID;

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
import no.safety.backend.user.dto.UsernameUpdateRequest;
import no.safety.backend.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Account management")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint for changing the username of a {@link StudentUser}.
     * Can only be used by a teacher account.
     * @param uid
     * @param request
     * @return
     */
    @PatchMapping("/{uid}/username")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Endpoint for changing the username of a student.", description = "Updates the students username. Must be called from a teacher account.")
    public ResponseEntity<UsernameUpdateResponse> updateStudentUsername(
            @PathVariable UUID uid,
            @Valid @RequestBody UsernameUpdateRequest request) {

        return ResponseEntity.ok(userService.updateStudentUsername(uid, request.username()));
    }

    /**
     * Endpoint for changing the username of a {@link User}
     * Can be used by both teachers and users.
     * @param uid
     * @param request
     * @return
     */
    @PatchMapping("/username")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Operation(summary = "Endpoint for changing the username of the logged in user.", description = "Updated the user's username.")
    public ResponseEntity<UsernameUpdateResponse> updateUsername(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UsernameUpdateRequest request) {

        return ResponseEntity.ok(userService.updateUsername(userPrincipal.getUserInternalId(), request.username()));
    }
}
