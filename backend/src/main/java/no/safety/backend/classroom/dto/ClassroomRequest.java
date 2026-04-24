package no.safety.backend.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ClassroomRequest(
    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    String name,
    @Size(max=1000, message = "Description cannot exceed 1000 characters.")
    String description,
    @NotNull(message = "World UID is required.")
    UUID worldUid
) {

}
