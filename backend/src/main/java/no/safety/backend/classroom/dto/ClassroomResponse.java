package no.safety.backend.classroom.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import no.safety.backend.classroom.model.Classroom;

public record ClassroomResponse(
    UUID uid,
    String name,
    String description,
    UUID worldUid,
    String code,
    List<OwnerSummary> owners,
    List<MemberSummary> members
) {

  public static ClassroomResponse from(Classroom c) {
    List<OwnerSummary> owners = c.getOwners().stream()
        .map(u -> new OwnerSummary(u.getPublicUuid(), u.getUsername()))
        .collect(Collectors.toList());

    List<MemberSummary> members = c.getMembers().stream()
        .map(u -> new MemberSummary(u.getPublicUuid(), u.getUsername(), u.getScore()))
        .collect(Collectors.toList());

    return new ClassroomResponse(c.getUid(), c.getName(), c.getDescription(), c.getWorldUid(), c.getCode(), owners,
        members);
  }

}
