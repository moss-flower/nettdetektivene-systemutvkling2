package no.safety.backend.classroom.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.exception.ConflictException;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.model.User;
import no.safety.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassroomService {

  private final ClassroomRepository classroomRepo;
  private final UserRepository userRepo;
  private final ClassroomCodeGenerator codegen;

  //TODO: Replace all UUID params with authentication eventually and use this to get the role.
  @Transactional
  public ClassroomResponse create(ClassroomRequest req, UUID callerUuid) {
    User owner = userRepo.findByPublicUuid(callerUuid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    //TODO: add better role check.
    if (!(owner instanceof TeacherUser)) {
      throw new ForbiddenException("User must be a teacher");
    }

    Classroom c = new Classroom();
    c.getOwners().add(owner);
    c.setName(req.name());
    c.setDescription(req.description());
    c.setCode(codegen.generateUniqueCode());
    return ClassroomResponse.from(classroomRepo.save(c));
  }

  @Transactional
  public ClassroomResponse update(Long classroomId, ClassroomRequest req, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    c.setName(req.name());
    c.setDescription(req.description());
    classroomRepo.save(c);
    return ClassroomResponse.from(c);
  }

  @Transactional
  public void delete(Long classroomId, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    classroomRepo.delete(c);
  }

  @Transactional
  public ClassroomResponse addOwner(Long classroomId, UUID newOwnerUuid, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    User newOwner = userRepo.findByPublicUuid(newOwnerUuid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    //TODO: Replace with better role check after user registration has implemented authentication tokens.
    if (!(newOwner instanceof TeacherUser)) {
      throw new ForbiddenException("New owner must be a teacher");
    }

    if (classroomRepo.existsByIdAndOwners_PublicUuid(classroomId, newOwnerUuid)) {
      throw new ConflictException("User is already an owner");
    }

    c.getOwners().add(newOwner);

    return ClassroomResponse.from(c);
  }

  @Transactional
  public ClassroomResponse removeOwner(Long classroomId, UUID ownerToRemoveUuid, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);

    if (c.getOwners().size() <= 1) {
      throw new ConflictException("Cannot remove the last owner of a classroom");
    }

    boolean removed = c.getOwners()
        .removeIf(user -> user.getPublicUuid().equals(ownerToRemoveUuid));

    if (!removed) {
      throw new NotFoundException("User is not an owner of this classroom");
    }

    return ClassroomResponse.from(c);
  }

  /**
   * Generates a new code for joining the classroom. Useful when codes are leaked.
   *
   * @param classroomId Classroom id
   * @param callerUuid  UUID of the calling user
   * @return ClassroomResponse
   */
  @Transactional
  public ClassroomResponse regenerateCode(Long classroomId, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    c.setCode(codegen.generateUniqueCode());
    classroomRepo.save(c);
    return ClassroomResponse.from(c);
  }

  @Transactional
  public ClassroomResponse joinByCode(String code, UUID callerUuid) {
    Classroom c = classroomRepo.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Classroom not found"));
    User member = userRepo.findByPublicUuid(callerUuid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!(member instanceof StudentUser)) {
      throw new ForbiddenException("User must be a student");
    }

    if (classroomRepo.existsByMembers_PublicUuid(callerUuid)) {
      throw new ConflictException("User is already a member");
    }

    c.getMembers().add(member);
    return ClassroomResponse.from(c);
  }

  @Transactional
  public ClassroomResponse removeMember(Long classroomId, UUID memberUuid, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    boolean removed = c.getMembers().removeIf(user -> user.getPublicUuid().equals(memberUuid));

    if (!removed) {
      throw new NotFoundException("User is not a member of this classroom");
    }
    return ClassroomResponse.from(c);
  }

  @Transactional
  public void leaveClassroom(Long classroomId, UUID callerUuid) {
    Classroom c = classroomRepo.findById(classroomId)
        .orElseThrow(() -> new NotFoundException("Could not find classroom"));

    boolean removed = c.getMembers().removeIf(user -> user.getPublicUuid().equals(callerUuid));

    if (!removed) {
      throw new NotFoundException("User is not a member of this classroom");
    }
  }

  private Classroom requireOwned(Long classroomId, UUID callerUuid) {
    if (classroomRepo.findById(classroomId).isEmpty()) {
      throw new NotFoundException("Could not find classroom");
    }
    Optional<Classroom> c = classroomRepo.findByIdAndOwners_PublicUuid(classroomId, callerUuid);
    if (c.isEmpty()) {
      throw new ForbiddenException("Caller is not an owner of the classroom");
    }
    return c.get();
  }
}
