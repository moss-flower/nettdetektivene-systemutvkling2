package no.safety.backend.avatar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.avatar.service.AvatarService;
import no.safety.backend.exception.NotFoundException;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AvatarController {

    
    private final AvatarService avatarService;

    /**
     * Allows a Student to update the Configuration for their Avatar.
     * Requires that one be logged in as a student.
     * 
     * @param request an AvatarConfigRequest dto containing permutation information.
     * @param principal Spring security principal for calling the user.
     * @return a response entity. Containing error message on failed update.
     */
    @Operation(
        summary = "Update student Avatar",
        description = "Allows a student to save changes made to their user Avatar",
        tags = {"Profile Management for Students"}
    )
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(
        @Valid @RequestBody AvatarConfigRequest request,
        @AuthenticationPrincipal UserPrincipal principal) {
            try {
                avatarService.updateAvatar(principal.getUserInternalId(), request);
            } catch (NotFoundException e) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(null);
        }
    
}
