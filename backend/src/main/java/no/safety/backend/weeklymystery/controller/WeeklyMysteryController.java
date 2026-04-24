package no.safety.backend.weeklymystery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.service.WeeklyMysteryService;

@RestController
@RequestMapping("/api/v1/WeeklyMystery")
@RequiredArgsConstructor
public class WeeklyMysteryController {

    private final WeeklyMysteryService weeklyMysteryService;


    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "/")
    public ResponseEntity<?> submitResponse(@Valid @RequestBody WeeklyMysterySubmissionCreateRequest request,
         @AuthenticationPrincipal UserPrincipal principal) {
            weeklyMysteryService.saveStudentSubmission(request, principal.getUserInternalId());
            return ResponseEntity.ok(null); 
         }

    
}
