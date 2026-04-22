package no.safety.backend.classroom.dto;

import java.util.List;
import java.util.stream.Collectors;
import no.safety.backend.classroom.model.Classroom;

public record ClassroomResponse(
    Long id,
    String name,
    String description,
    String worldUid,
    String code,
    List<OwnerSummary> owners,
    List<MemberSummary> members
) {

  public static ClassroomResponse from(Classroom c) {
    List<OwnerSummary> owners = c.getOwners().stream()
        .map(u -> new OwnerSummary(u.getPublicUuid(), u.getUsername()))
        .collect(Collectors.toList());

    List<MemberSummary> members = c.getMembers().stream()
        .map(u -> new MemberSummary(u.getPublicUuid(), u.getUsername()))
        .collect(Collectors.toList());

    return new ClassroomResponse(c.getId(), c.getName(), c.getDescription(), c.getWorldUid(), c.getCode(), owners,
        members);
  }

}
