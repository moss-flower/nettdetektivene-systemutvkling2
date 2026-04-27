package no.safety.backend.classroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import no.safety.backend.auth.model.UserPrincipal;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.service.ClassroomService;
import no.safety.backend.weeklymystery.service.WeeklyMysteryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Classroom controller that defines API endpoints for managing classrooms. All endpoints require an
 * authenticated caller, and mutating operations require the caller to be an owner of the
 * classroom.
 */
@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

  private final ClassroomService classroomService;

  /**
   * Creates a new classroom owned by the caller. Join code is automatically generated.
   *
   * @param request        the classroom name and description.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the created classroom, including the join code.
   * @throws no.safety.backend.exception.NotFoundException  if the calling user is not found
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not a teacher
   */
  @Operation(
      summary = "Create a new classroom",
      description = "Creates a new classroom owned by the caller. A join code is automatically generated.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClassroomResponse create(
      @Valid @RequestBody ClassroomRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.create(request, getCallerUuid(principal));
  }

  /**
   * Updates the name and description of an existing classroom.
   *
   * @param classroomId    the id of the classroom to be updated.
   * @param request        the new name and description of the classroom.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the updated classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   */
  @Operation(
      summary = "Update a classroom",
      description = "Updates the name and description of an existing classroom. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/{classroomId}")
  public ClassroomResponse update(
      @PathVariable UUID classroomId,
      @Valid @RequestBody ClassroomRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.update(classroomId, request, getCallerUuid(principal));
  }

  /**
   * Deletes an existing classroom.
   *
   * @param classroomId    the id of the classroom to be deleted.
   * @param principal Spring Security principal for the calling user.
   * @return an empty 204 No content response.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   */
  @Operation(
      summary = "Delete a classroom",
      description = "Deletes an existing classroom. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{classroomId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID classroomId,
      @AuthenticationPrincipal UserPrincipal principal) {
    classroomService.delete(classroomId, getCallerUuid(principal));
    return ResponseEntity.noContent().build();
  }

  /**
   * Adds an owner to the classroom.
   *
   * @param classroomId    the id of the classroom
   * @param userUuid       the UUID of the user to be added as an owner.
   * @param principal Spring Security principal for the calling user.
   * @return a response of the classroom including the new owner
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the new owner user could not be
   *                                                        found.
   * @throws no.safety.backend.exception.ForbiddenException if the new owner user is not a teacher.
   * @throws no.safety.backend.exception.ConflictException  if the new owner user is already an
   *                                                        owner.
   */
  @Operation(
      summary = "Add an owner to a classroom",
      description = "Adds a teacher as an owner of the classroom. Requires the caller to be an existing owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/{classroomId}/owners/{userUuid}")
  public ClassroomResponse addOwner(
      @PathVariable UUID classroomId,
      @PathVariable UUID userUuid,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.addOwner(classroomId, userUuid, getCallerUuid(principal));
  }

  /**
   * Removes an owner from a classroom. It is not possible to remove the last owner, as the
   * classroom needs at least one owner.
   *
   * @param classroomId    id of the classroom.
   * @param userUuid       the UUID of the owner to remove.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the classroom excluding the removed owner.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   * @throws no.safety.backend.exception.ConflictException  if trying to remove the last owner.
   * @throws no.safety.backend.exception.NotFoundException  if the user to remove is not an owner of
   *                                                        the classroom.
   */
  @Operation(
      summary = "Remove an owner from a classroom",
      description = "Removes a teacher as an owner of the classroom. The last owner cannot be removed. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{classroomId}/owners/{userUuid}")
  public ClassroomResponse removeOwner(
      @PathVariable UUID classroomId,
      @PathVariable UUID userUuid,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.removeOwner(classroomId, userUuid, getCallerUuid(principal));
  }

  /**
   * Generates a new join code for the classroom and updates it.
   *
   * @param classroomId    id of the classroom.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the classroom with the new join code.
   */
  @Operation(
      summary = "Regenerate join code",
      description = "Generates a new join code for the classroom, replacing the old one. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/{classroomId}/code")
  public ClassroomResponse regenerateCode(
      @PathVariable UUID classroomId,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.regenerateCode(classroomId, getCallerUuid(principal));
  }

  /**
   * Gets all classrooms owned by the caller.
   *
   * @param principal Spring Security principal for the calling user.
   * @return a set of responses containing classrooms the caller owns.
   */
  @Operation(
      summary = "Get all owned classrooms",
      description = "Returns all classrooms owned by the logged-in teacher.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping
  public Set<ClassroomResponse> getOwnedClassrooms(@AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.getOwnedClassrooms(getCallerUuid(principal));
  }

  /**
   * Gets a classroom by its ID.
   *
   * @param classroomId    of the classroom to get.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the specified classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found
   * @throws no.safety.backend.exception.ForbiddenException if the caller does not own the
   *                                                        classroom.
   */
  @Operation(
      summary = "Get a classroom by ID",
      description = "Returns a specific classroom by its ID. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping("/{classroomId}")
  public ClassroomResponse getClassroom(@PathVariable UUID classroomId,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.getClassroom(classroomId, getCallerUuid(principal));
  }

  /**
   * Change the weekly mystery for a classroom.
   *
   * @param classroomUid the Public Id of the classroom, used to resolve a classroom object from database.
   * @param principal user info provided by spring.
   * @param weeklyMysterUuid the public id of the selected Weekly Mystery, for database resolution.
   * @return an empty response object on success, error on failure.
   */
  @PreAuthorize("hasRole('TEACHER')")
  @PatchMapping("/{classroomId}/ukens-mysterium")
  public ResponseEntity<?> updateClassroom(@PathVariable UUID classroomUid,
                                           @AuthenticationPrincipal UserPrincipal principal,
                                           @RequestBody UUID weeklyMysterUuid) {
    classroomService.updateWeeklyMystery(classroomUid, principal.getUserPublicUuid(), weeklyMysterUuid);
    return ResponseEntity.ok(null);
  })

  /**
   * Gets a students classroom.
   *
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the classroom of the student.
   * @throws no.safety.backend.exception.NotFoundException if the student is not found or if it is
   *                                                       not in a classroom.
   */
  @Operation(
      summary = "Get my classroom",
      description = "Returns the classroom that the logged-in student is a member of.",
      tags = {"Classroom management for students"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/me")
  public ClassroomResponse getMyClassroom(@AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.getMyClassroom(getCallerUuid(principal));
  }

  /**
   * Allows a student to join a classroom using the join code. A student can only be a member of one
   * classroom at a time.
   *
   * @param code           the join code of the classroom.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the joined classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom or user is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the caller is not a student.
   * @throws no.safety.backend.exception.ConflictException  if the student is already in a
   *                                                        classroom.
   */
  @Operation(
      summary = "Join a classroom",
      description = "Allows a student to join a classroom using a join code. A student can only be a member of one classroom at a time.",
      tags = {"Classroom management for students"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/join/{code}")
  public ClassroomResponse joinByCode(
      @PathVariable String code,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.joinByCode(code, getCallerUuid(principal));
  }

  /**
   * Removes a student member from a classroom. Requires the caller to be an owner.
   *
   * @param classroomId    the id of the classroom.
   * @param userUuid       the UUID of the member to remove.
   * @param principal Spring Security principal for the calling user.
   * @return a response containing the classroom without the removed member.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom or member is not
   *                                                        found.
   * @throws no.safety.backend.exception.ForbiddenException if the caller is not an owner.
   */
  @Operation(
      summary = "Remove a student from a classroom",
      description = "Removes a student member from a classroom. Requires the caller to be an owner.",
      tags = {"Classroom management for teachers"}
  )
  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/{classroomId}/members/{userUuid}")
  public ClassroomResponse removeMember(
      @PathVariable UUID classroomId,
      @PathVariable UUID userUuid,
      @AuthenticationPrincipal UserPrincipal principal) {
    return classroomService.removeMember(classroomId, userUuid, getCallerUuid(principal));
  }

  /**
   * Allows a student to leave a classroom they are a member of.
   *
   * @param classroomId    the id of the classroom to leave.
   * @param principal Spring Security principal for the calling user.
   * @return an empty 204 No Content response.
   * @throws no.safety.backend.exception.NotFoundException if the classroom is not found or the
   *                                                       caller is not a member.
   */
  @Operation(
      summary = "Leave a classroom",
      description = "Allows the logged-in student to leave a classroom they are a member of.",
      tags = {"Classroom management for students"}
  )
  @PreAuthorize("hasRole('STUDENT')")
  @DeleteMapping("/{classroomId}/members/me")
  public ResponseEntity<Void> leaveClassroom(
      @PathVariable UUID classroomId,
      @AuthenticationPrincipal UserPrincipal principal) {
    classroomService.leaveClassroom(classroomId, getCallerUuid(principal));
    return ResponseEntity.noContent().build();
  }

  private UUID getCallerUuid(UserPrincipal principal) {
    return principal.user().getPublicUuid();
  }
}
