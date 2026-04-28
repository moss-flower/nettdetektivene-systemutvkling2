package no.safety.backend.weeklymystery.controller;

import java.util.HashSet;

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
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptResponse;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.service.WeeklyMysteryService;

@RestController
@RequestMapping("/api/v1/WeeklyMystery")
@RequiredArgsConstructor
public class WeeklyMysteryController {

    private final WeeklyMysteryService weeklyMysteryService;

    /**
     * Allows students to submit files related to a given Weekly Mystery prompt
     * @param request an object containing a prompt id, file and message.
     * @param principal user information (Automatically provided by Spring, ignore)
     * @return an empty response entity indicating success or failure.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "/")
    public ResponseEntity<?> submitResponse(@Valid @RequestBody WeeklyMysterySubmissionCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        weeklyMysteryService.saveStudentSubmission(request, principal.getUserInternalId());
        return ResponseEntity.ok(null);
    }

    /**
     * Allows teachers to submit new Weekly Mystery prompts for persistence.
     * @param request A request containing clue, title, prompt, selected date and selected classroom (public UUID's)
     * @param principal user information (Automatically provided by spring, Ignore)
     * @return a response entity containing the submitted prompt information plus public UUID for prompt on success.
     */
    @Operation(
        summary = "Create Prompt",
        description = "Endpoint allows teachers to create a new Weekly Mystery Prompt"
    )
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(value = "/prompts")
    public ResponseEntity<WeeklyMysteryPromptResponse> submitPrompt(
            @Valid @RequestBody WeeklyMysteryPromptCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(weeklyMysteryService.savePrompt(request, principal.getUserInternalId()));
        
    }

    /**
     * Allows teachers to retrieve a list of all prompts that they have created.
     * @param principal user information (Automatically provided by spring)
     * @return a response entity a set of Prompt Responses
     */
    @Operation(
        summary = "Retrieve prompts",
        description = "Endpoint allows teachers to retrieve a list of all prompts connected to their account."
    )
    @PreAuthorize("hasRole('TEACHER')")
    @RequestMapping(value = "/prompts")
    public ResponseEntity<HashSet<WeeklyMysteryPromptResponse>> fetchPrompts(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(weeklyMysteryService.fetchUsersPrompts(principal.getUserInternalId()));
    }
}
