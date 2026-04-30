package no.safety.backend.weeklymystery.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysteryPromptResponse;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionCreateRequest;
import no.safety.backend.weeklymystery.dto.WeeklyMysterySubmissionResponse;
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
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitResponse(@Valid @ModelAttribute WeeklyMysterySubmissionCreateRequest request,
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
     * Provides an endpoint for retrieving a list of submissions related to a given
     * Weekly Mystery Prompt. 
     * @param promptId the public UUID of the prompt you want to view submissions for.
     * @return a set of Weekly Mystery Submission DTOs.
     */
    @Operation(
        summary = "Get a set of submissions",
        description = "Get a set of submissions related to a weekly mystery prompt."
    )
    @RequestMapping(value = "/{promptId}/submissions", method = RequestMethod.GET)
    public ResponseEntity<Set<WeeklyMysterySubmissionResponse>> getSubmissions(
        @PathVariable UUID promptId 
        ) {
            return ResponseEntity.ok(weeklyMysteryService.getWeeklyMysterySubmissionResponses(promptId));
    
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
    @RequestMapping(value = "/prompts", method = RequestMethod.GET)
    public ResponseEntity<HashSet<WeeklyMysteryPromptResponse>> fetchPrompts(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(weeklyMysteryService.fetchUsersPrompts(principal.getUserInternalId()));
    }

    /**
     * Provides an endpoint for deleting a specific submission.
     * The requesting user must be either the submitting student or an authorized teacher.
     *
     * @param submissionId the public UUID of the submission to delete.
     */
    @Operation(
    summary = "Delete a submission",
    description = "Delete a submission by its UUID. Requester must be the submitting student or an authorized teacher."
)
    @DeleteMapping("/{promptId}/submissions/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable UUID promptId,
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        weeklyMysteryService.deleteSubmission(submissionId, principal.getUserInternalId());
        return ResponseEntity.noContent().build();
    }


    /**
     * Provides an endpoint for deleting a specific Weekly Mystery Prompt.
     * The requesting user must be the teacher who created the prompt.
     *
     * @param promptId the public UUID of the prompt to delete.
     */
    @Operation(
        summary = "Delete a prompt",
        description = "Delete a Weekly Mystery prompt by its UUID. Requester must be the teacher who created it."
    )
    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deletePrompt(
            @PathVariable UUID promptId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        weeklyMysteryService.delete(promptId, principal.getUserInternalId());
        return ResponseEntity.noContent().build();
    }
}
