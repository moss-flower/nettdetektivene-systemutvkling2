package no.safety.backend.classroom.dto;

import java.util.List;
import java.util.stream.Collectors;
import no.safety.backend.classroom.model.Classroom;

public record ClassroomResponse(
    Long id,
    String name,
    String description,
    String code,
    List<OwnerSummary> owners
) {

  public static ClassroomResponse from(Classroom c) {
    List<OwnerSummary> owners = c.getOwners().stream()
        .map(u -> new OwnerSummary(u.getPublicUuid(), u.getName()))
        .collect(Collectors.toSet());

    return new ClassroomResponse(c.getId(), c.getName(), c.getDescription(), c.getCode(), owners);
  }

}
