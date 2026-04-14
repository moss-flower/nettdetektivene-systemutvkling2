package no.safety.backend.classroom.dto;

public record ClassroomResponse(
    Long id,
    String name,
    String description,
    String code
    //TODO: owner should be included

) {

}
