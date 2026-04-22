package no.safety.backend.classroom.service;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.dto.ClassroomRequest;
import no.safety.backend.classroom.dto.ClassroomResponse;
import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import no.safety.backend.exception.ConflictException;
import no.safety.backend.exception.NotFoundException;
import no.safety.backend.exception.domain.NotClassroomOwnerException;
import no.safety.backend.exception.domain.StudentNotInClassException;
import no.safety.backend.notification.dto.NotificationEvent;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.model.TeacherUser;
import no.safety.backend.user.repository.StudentUserRepository;
import no.safety.backend.user.repository.TeacherUserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassroomService {

  private final ClassroomRepository classroomRepo;
  private final TeacherUserRepository teacherRepo;
  private final StudentUserRepository studentRepo;
  private final ClassroomCodeGenerator codegen;
  private final ApplicationEventPublisher eventPublisher; // use this for publishing notifications for the teacher

  @Transactional
  public ClassroomResponse create(ClassroomRequest req, UUID callerUuid) {
    TeacherUser owner = requireTeacher(callerUuid);

    Classroom c = new Classroom();
    c.getOwners().add(owner);
    c.setName(req.name());
    c.setDescription(req.description());
    c.setWorldUid(req.worldUid());
    c.setCode(codegen.generateUniqueCode());
    return ClassroomResponse.from(classroomRepo.save(c));
  }

  @Transactional
  public ClassroomResponse update(Long classroomId, ClassroomRequest req, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    c.setName(req.name());
    c.setDescription(req.description());
    c.setWorldUid(req.worldUid());
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
    TeacherUser newOwner = requireTeacher(newOwnerUuid);

    if (classroomRepo.existsByIdAndOwners_PublicUuid(classroomId, newOwnerUuid)) {
      throw new ConflictException("Teacher is already an owner");
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
      throw new NotClassroomOwnerException("Teacher is not an owner of this classroom");
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

    StudentUser member = requireStudent(callerUuid);

    if (member.getClassroom() != null) {
      throw new ConflictException("Student is already a member of a classroom");
    }

    member.setClassroom(c);
    c.getMembers().add(member);
    return ClassroomResponse.from(c);
  }

  @Transactional
  public ClassroomResponse removeMember(Long classroomId, UUID memberUuid, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);

    StudentUser member = requireStudent(memberUuid);

    if (member.getClassroom() == null || !member.getClassroom().getId().equals(classroomId)) {
      throw new StudentNotInClassException("Student is not a member of this classroom");
    }

    member.setClassroom(null);
    c.getMembers().remove(member);

    eventPublisher.publishEvent(new NotificationEvent(memberUuid, classroomId, 0));
    return ClassroomResponse.from(c);
  }

  @Transactional
  public void leaveClassroom(Long classroomId, UUID callerUuid) {
    classroomRepo.findById(classroomId)
        .orElseThrow(() -> new NotFoundException("Could not find classroom"));

    StudentUser member = studentRepo.findByPublicUuid(callerUuid)
        .orElseThrow(() -> new NotFoundException("Student not found"));

    if (member.getClassroom() == null || !member.getClassroom().getId().equals(classroomId)) {
      throw new StudentNotInClassException("Student is not a member of this classroom");
    }

    member.setClassroom(null);
    studentRepo.save(member);
  }

  @Transactional(readOnly = true)
  public Set<ClassroomResponse> getOwnedClassrooms(UUID callerUuid) {
    return classroomRepo.findAllByOwners_PublicUuid(callerUuid).stream()
        .map(ClassroomResponse::from)
        .collect(java.util.stream.Collectors.toSet());
  }

  @Transactional(readOnly = true)
  public ClassroomResponse getClassroom(Long classroomId, UUID callerUuid) {
    Classroom c = requireOwned(classroomId, callerUuid);
    return ClassroomResponse.from(c);
  }

  @Transactional(readOnly = true)
  public ClassroomResponse getMyClassroom(UUID callerUuid) {
    StudentUser student = studentRepo.findByPublicUuid(callerUuid)
        .orElseThrow(() -> new NotFoundException("Student not found"));

    if (student.getClassroom() == null) {
      throw new StudentNotInClassException("Student is not in a classroom");
    }

    return ClassroomResponse.from(student.getClassroom());
  }

  private Classroom requireOwned(Long classroomId, UUID callerUuid) {
    if (classroomRepo.findById(classroomId).isEmpty()) {
      throw new NotFoundException("Could not find classroom");
    }
    return classroomRepo.findByIdAndOwners_PublicUuid(classroomId, callerUuid).orElseThrow(() -> new NotClassroomOwnerException("Caller is not an owner of the classroom"));
  }

  private TeacherUser requireTeacher(UUID uuid) {
    return teacherRepo.findByPublicUuid(uuid).orElseThrow(() -> new NotFoundException("Teacher not found"));
  }

  private StudentUser requireStudent(UUID uuid) {
    return studentRepo.findByPublicUuid(uuid).orElseThrow(() -> new NotFoundException("Student not found"));
  }
}
