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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Profile Management for Students")
public class AvatarController {

    
    private final AvatarService avatarService;

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
    public ResponseEntity<Void> updateAvatar(
        @Valid @RequestBody AvatarConfigRequest request,
        @AuthenticationPrincipal UserPrincipal principal) {
            avatarService.updateAvatar(principal.getUserInternalId(), request);
            return ResponseEntity.ok().build();
    }
    
}
