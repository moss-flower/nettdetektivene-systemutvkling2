package no.safety.backend.world.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record WorldRequest(
    @NotBlank(message = "title is required.")
    @Size(max = 100, message = "title cannot exceed 100 characters.")
    String title,

    @NotBlank(message = "description is required.")
    @Size(max = 2000, message = "description cannot exceed 2000 characters.")
    String description,

    @NotBlank(message = "image is required.")
    @Size(max = 1000, message = "image cannot exceed 1000 characters.")
    String image,

    @NotNull(message = "stationUids is required.")
    List<UUID> stationUids
) {
}
