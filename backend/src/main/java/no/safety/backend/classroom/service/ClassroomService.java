package no.safety.backend.classroom.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.exception.ConflictException;
import no.safety.backend.exception.ForbiddenException;
import no.safety.backend.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomService {

  private final ClassroomRepository classroomRepo;
  private final UserRepository userRepo;
  private final ClassroomCodeGenerator codegen;

  public ClassroomService(ClassroomRepository classroomRepo, UserRepository userRepo) {
    this.classroomRepo = classroomRepo;
    this.userRepo = userRepo;
    this.codegen = new ClassroomCodeGenerator(classroomRepo);
  }

  @Transactional
  public ClassroomResponse create(ClassroomRequest req, UUID callerUuid) {
    User owner = userRepo.findByPublicUuid(callerUuid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!(owner instanceof TeacherUser)) {
      throw new ForbiddenException("User must be a teacher");
    }

    Classroom c = new Classroom();
    c.setOwner(owner);
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

    if (!(newOwner instanceof TeacherUser)) {
      throw new ForbiddenException("New owner must be a teacher");
    }

    boolean added = c.getOwners().add(newOwner);
    if (!added) {
      throw new ConflictException("User is already added");
    }

    return ClassroomResponse.from(c);
  }

  @Transactional
  public ClassroomResponse removeOwner(Long classroomId, UUID ownerToRemoveUuid, Long callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);

    if (c.getOwners().size() <= 1) {
      throw new ConflictException("Cannot remove the last owner");
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
  public ClassroomResponse regenerateCode(Long classroomId, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    c.setCode(codegen.generateUniqueCode());
    return ClassroomResponse.from(c);
  }

  private Classroom requireOwned(Long classroomId, UUID callerUuid) {
    Classroom c = classroomRepo.findById(classroomId)
        .orElseThrow(() -> new NotFoundException("Classroom not found"));

    if (c.getOwners().stream().noneMatch(owner -> owner.getPublicUuid().equals(callerUuid))) {
      throw new ForbiddenException("User is not an owner");
    }

    return c;
  }
}
