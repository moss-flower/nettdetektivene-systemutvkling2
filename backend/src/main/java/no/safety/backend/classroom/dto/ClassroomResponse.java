package no.safety.backend.classroom.dto;

import java.util.Set;
import java.util.stream.Collectors;
import no.safety.backend.classroom.model.Classroom;

public record ClassroomResponse(
    Long id,
    String name,
    String description,
    String code,
    Set<OwnerSummary> owners
) {

  public static ClassroomResponse from(Classroom c) {
    Set<OwnerSummary> owners = c.getOwners().stream()
        .map(u -> new OwnerSummary(u.getPublicUuid(), u.getName()))
        .collect(Collectors.toSet());

    return new ClassroomResponse(c.getId(), c.getName(), c.getDescription(), c.getCode(), owners);
  }

}
