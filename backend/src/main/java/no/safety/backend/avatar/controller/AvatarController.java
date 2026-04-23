package no.safety.backend.avatar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.avatar.service.AvatarService;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AvatarController {

    
    private final AvatarService avatarService;

    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(
        @Valid @RequestBody AvatarConfigRequest request,
        @AuthenticationPrincipal UserPrincipal principal) {
            try {
                avatarService.updateAvatar(principal.getUserInternalId(), request);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e);
            }
            
            return ResponseEntity.ok(null);
        }
    
}
