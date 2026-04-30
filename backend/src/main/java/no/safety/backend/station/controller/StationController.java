package no.safety.backend.station.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.station.dto.StationDataResponse;
import no.safety.backend.station.dto.StationRequest;
import no.safety.backend.station.dto.StationResponse;
import no.safety.backend.station.dto.StudentStationDataResponse;
import no.safety.backend.station.service.StationService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

  private final StationService stationService;
  private final StudentUserRepository studentUserRepository;

  @Operation(summary = "Create a new station", tags = {"Stations"})
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StationResponse create(@Valid @RequestBody StationRequest request) {
    return stationService.create(request);
  }

  @Operation(summary = "Get all stations", tags = {"Stations"})
  @GetMapping
  public List<StationResponse> getAllStations() {
    return stationService.getAllStations();
  }

  @Operation(summary = "Get station data for logged-in student", tags = {"Stations"})
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  @GetMapping("/me/data")
  public StudentStationDataResponse getMyStationData(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(required = false) UUID worldUid) {
    if (isTeacher(principal)) {
      if (worldUid == null) {
        throw new NotFoundException("World uid is required for teacher preview");
      }

      return stationService.getTeacherPreviewStationData(worldUid);
    }

    return stationService.getMyStationData(getStudent(principal));
  }

  @Operation(summary = "Get station by UID", tags = {"Stations"})
  @GetMapping("/{uid}")
  public StationResponse getStationByUid(@PathVariable UUID uid) {
    return stationService.getStationByUid(uid);
  }

  @Operation(summary = "Get station data by UID for logged-in student", tags = {"Stations"})
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  @GetMapping("/{uid}/data")
  public StationDataResponse getStationDataByUid(
      @PathVariable UUID uid,
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(required = false) UUID worldUid) {
    if (isTeacher(principal)) {
      return stationService.getTeacherPreviewStationData(uid, worldUid);
    }

    return stationService.getStationData(uid, getStudent(principal));
  }

  @Operation(summary = "Update station by UID", tags = {"Stations"})
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{uid}")
  public StationResponse updateStationByUid(@PathVariable UUID uid, @Valid @RequestBody StationRequest request) {
    return stationService.update(uid, request);
  }

  @Operation(summary = "Delete station by UID", tags = {"Stations"})
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{uid}")
  public ResponseEntity<Void> deleteStationByUid(@PathVariable UUID uid) {
    stationService.delete(uid);
    return ResponseEntity.noContent().build();
  }

  private StudentUser getStudent(UserPrincipal principal) {
    return studentUserRepository.findByPublicUuid(principal.getUserPublicUuid())
        .orElseThrow(() -> new NotFoundException("Student not found"));
  }

  private boolean isTeacher(UserPrincipal principal) {
    return principal.user().getRoles().stream()
        .anyMatch(role -> role.getType() == RoleType.ROLE_TEACHER);
  }
}
