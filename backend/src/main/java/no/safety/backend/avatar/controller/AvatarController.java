package no.safety.backend.avatar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.avatar.dto.AvatarConfigResponse;
import no.safety.backend.avatar.dto.AvatarOptionsResponse;
import no.safety.backend.avatar.service.AvatarService;

/**
 * REST controller for managing student avatar configurations.
 * Provides endpoints for retrieving, updating, and fetching available options for avatar customization.
 * All endpoints require the user to have the STUDENT role.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Profile Management for Students")
public class AvatarController {

    private final AvatarService avatarService;

    /**
     * Retrieves the avatar configuration for the currently authenticated student.
     *
     * @param principal the authenticated user principal containing user details
     * @return the avatar configuration response containing the student's current avatar settings
     */
    @Operation(
        summary = "Get student Avatar",
        description = "Returns the logged-in student's avatar configuration",
        tags = {"Profile Management for Students"}
    )
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/avatar")
    public AvatarConfigResponse getAvatar(
        @AuthenticationPrincipal UserPrincipal principal) {
            return avatarService.getAvatar(principal.getUserInternalId());
    }

    /**
     * Retrieves all available avatar customization options.
     * Returns lists of available hair styles, hair colors, outfits, eye colors, and skin colors
     * that students can choose from when customizing their avatar.
     *
     * @return response containing all available avatar customization options
     */
    @Operation(
        summary = "Get avatar customization options",
        description = "Returns the available enum options for avatar customization",
        tags = {"Profile Management for Students"}
    )
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/avatar/options")
    public AvatarOptionsResponse getAvatarOptions() {
        return AvatarOptionsResponse.fromEnums();
    }

    /**
     * Updates the avatar configuration for the currently authenticated student.
     * Validates the request body and saves the new avatar settings to the database.
     *
     * @param request the avatar configuration request containing the new avatar settings
     * @param principal the authenticated user principal containing user details
     * @return a ResponseEntity with HTTP 200 OK on successful update
     */
    @Operation(
        summary = "Update student Avatar",
        description = "Allows a student to save changes made to their user Avatar",
        tags = {"Profile Management for Students"}
    )
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/avatar")
    public ResponseEntity<Void> updateAvatar(
        @Valid @RequestBody AvatarConfigRequest request,
        @AuthenticationPrincipal UserPrincipal principal) {
            avatarService.updateAvatar(principal.getUserInternalId(), request);
            return ResponseEntity.ok().build();
    }
    
}
