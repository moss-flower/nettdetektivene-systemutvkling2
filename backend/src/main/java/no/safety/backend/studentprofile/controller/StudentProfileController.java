package no.safety.backend.studentprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.studentprofile.dto.StudentProfileResponse;
import no.safety.backend.studentprofile.service.StudentProfileService;
import no.safety.backend.user.model.StudentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentProfileController {

  private final StudentProfileService studentProfileService;

  @Operation(
      summary = "Get my student profile",
      description = "Returns profile details and progression summary for the currently logged-in student.",
      tags = {"Student profile"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/me/profile")
  public StudentProfileResponse getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
    return studentProfileService.getProfile((StudentUser) principal.user());
  }

  @Operation(
      summary = "Get student profile",
      description = "Returns profile details and progression summary for a selected student. Used by teachers for classroom progress.",
      tags = {"Student profile"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping("/{studentUid}/profile")
  public StudentProfileResponse getStudentProfile(@PathVariable UUID studentUid) {
    return studentProfileService.getProfile(studentUid);
  }
}
