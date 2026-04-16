package no.safety.backend.classroom.controller;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.service.ClassroomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Classroom controller that defines API endpoints for managing classrooms. All endpoints require an
 * authenticated caller, and mutating operations require the caller to be an owner of the
 * classroom.
 */
@RestController
@RequestMapping("/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

  private final ClassroomService classroomService;

  /**
   * Creates a new classroom owned by the caller. Join code is automatically generated.
   *
   * @param request        the classroom name and description.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the created classroom, including the join code.
   * @throws no.safety.backend.exception.NotFoundException  if the calling user is not found
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not a teacher
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClassroomResponse create(
      @Valid @RequestBody ClassroomRequest request,
      Authentication authentication) {
    return classroomService.create(request, getCallerUuid(authentication));
  }

  /**
   * Updates the name and description of an existing classroom.
   *
   * @param classroomId    the id of the classroom to be updated.
   * @param request        the new name and description of the classroom.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the updated classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   */
  @PutMapping("/{classroomId}")
  public ClassroomResponse update(
      @PathVariable Long classroomId,
      @Valid @RequestBody ClassroomRequest request,
      Authentication authentication) {
    return classroomService.update(classroomId, request, getCallerUuid(authentication));
  }

  /**
   * Deletes an existing classroom.
   *
   * @param classroomId    the id of the classroom to be deleted.
   * @param authentication Spring Security context for the calling user.
   * @return an empty 204 No content response.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   */
  @DeleteMapping("/{classroomId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long classroomId,
      Authentication authentication) {
    classroomService.delete(classroomId, getCallerUuid(authentication));
    return ResponseEntity.noContent().build();
  }

  /**
   * Adds an owner to the classroom.
   *
   * @param classroomId    the id of the classroom
   * @param userUuid       the UUID of the user to be added as an owner.
   * @param authentication Spring Security context for the calling user.
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
  @PostMapping("/{classroomId}/owners/{userUuid}")
  public ClassroomResponse addOwner(
      @PathVariable Long classroomId,
      @PathVariable UUID userUuid,
      Authentication authentication) {
    return classroomService.addOwner(classroomId, userUuid, getCallerUuid(authentication));
  }

  /**
   * Removes an owner from a classroom. It is not possible to remove the last owner, as the
   * classroom needs at least one owner.
   *
   * @param classroomId    id of the classroom.
   * @param userUuid       the UUID of the owner to remove.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the classroom excluding the removed owner.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the calling user is not an owner of
   *                                                        the classroom.
   * @throws no.safety.backend.exception.ConflictException  if trying to remove the last owner.
   * @throws no.safety.backend.exception.NotFoundException  if the user to remove is not an owner of
   *                                                        the classroom.
   */
  @DeleteMapping("/{classroomId}/owners/{userUuid}")
  public ClassroomResponse removeOwner(
      @PathVariable Long classroomId,
      @PathVariable UUID userUuid,
      Authentication authentication) {
    return classroomService.removeOwner(classroomId, userUuid, getCallerUuid(authentication));
  }

  /**
   * Generates a new join code for the classroom and updates it.
   *
   * @param classroomId    id of the classroom.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the classroom with the new join code.
   */
  @PostMapping("/{classroomId}/code")
  public ClassroomResponse regenerateCode(
      @PathVariable Long classroomId,
      Authentication authentication) {
    return classroomService.regenerateCode(classroomId, getCallerUuid(authentication));
  }

  /**
   * Gets all classrooms owned by the caller.
   *
   * @param authentication Spring Security context for the calling user.
   * @return a set of responses containing classrooms the caller owns.
   */
  @GetMapping
  public Set<ClassroomResponse> getOwnedClassrooms(Authentication authentication) {
    return classroomService.getOwnedClassrooms(getCallerUuid(authentication));
  }

  /**
   * Gets a classroom by its ID.
   *
   * @param classroomId    of the classroom to get.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the specified classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom is not found
   * @throws no.safety.backend.exception.ForbiddenException if the caller does not own the
   *                                                        classroom.
   */
  @GetMapping("/{classroomId}")
  public ClassroomResponse getClassroom(@PathVariable Long classroomId,
      Authentication authentication) {
    return classroomService.getClassroom(classroomId, getCallerUuid(authentication));
  }

  /**
   * Gets a students classroom.
   *
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the classroom of the student.
   * @throws no.safety.backend.exception.NotFoundException if the student is not found or if it is
   *                                                       not in a classroom.
   */
  @GetMapping("/me")
  public ClassroomResponse getMyClassroom(Authentication authentication) {
    return classroomService.getMyClassroom(getCallerUuid(authentication));
  }

  /**
   * Allows a student to join a classroom using the join code. A student can only be a member of one
   * classroom at a time.
   *
   * @param code           the join code of the classroom.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the joined classroom.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom or user is not found.
   * @throws no.safety.backend.exception.ForbiddenException if the caller is not a student.
   * @throws no.safety.backend.exception.ConflictException  if the student is already in a
   *                                                        classroom.
   */
  @PostMapping("/join/{code}")
  public ClassroomResponse joinByCode(
      @PathVariable String code,
      Authentication authentication) {
    return classroomService.joinByCode(code, getCallerUuid(authentication));
  }

  /**
   * Removes a student member from a classroom. Requires the caller to be an owner.
   *
   * @param classroomId    the id of the classroom.
   * @param userUuid       the UUID of the member to remove.
   * @param authentication Spring Security context for the calling user.
   * @return a response containing the classroom without the removed member.
   * @throws no.safety.backend.exception.NotFoundException  if the classroom or member is not
   *                                                        found.
   * @throws no.safety.backend.exception.ForbiddenException if the caller is not an owner.
   */
  @DeleteMapping("/{classroomId}/members/{userUuid}")
  public ClassroomResponse removeMember(
      @PathVariable Long classroomId,
      @PathVariable UUID userUuid,
      Authentication authentication) {
    return classroomService.removeMember(classroomId, userUuid, getCallerUuid(authentication));
  }

  /**
   * Allows a student to leave a classroom they are a member of.
   *
   * @param classroomId    the id of the classroom to leave.
   * @param authentication Spring Security context for the calling user.
   * @return an empty 204 No Content response.
   * @throws no.safety.backend.exception.NotFoundException if the classroom is not found or the
   *                                                       caller is not a member.
   */
  @DeleteMapping("/{classroomId}/members/me")
  public ResponseEntity<Void> leaveClassroom(
      @PathVariable Long classroomId,
      Authentication authentication) {
    classroomService.leaveClassroom(classroomId, getCallerUuid(authentication));
    return ResponseEntity.noContent().build();
  }

  private UUID getCallerUuid(Authentication authentication) {
    return UUID.fromString(authentication.getName());
  }
}