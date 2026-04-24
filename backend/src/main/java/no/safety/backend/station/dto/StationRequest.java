package no.safety.backend.station.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record StationRequest(
    @NotBlank(message = "title is required.")
    @Size(max = 100, message = "title cannot exceed 100 characters.")
    String title,

    @NotBlank(message = "description is required.")
    @Size(max = 2000, message = "description cannot exceed 2000 characters.")
    String description,

    @NotBlank(message = "image is required.")
    @Size(max = 1000, message = "image cannot exceed 1000 characters.")
    String image,

    @Size(max = 20, message = "backgroundColor cannot exceed 20 characters.")
    String backgroundColor,

    @NotEmpty(message = "A station must contain at least one exercise.")
    List<UUID> exerciseUids,

    @NotEmpty(message = "A station must specify if it is a boss station")
    boolean isBoss
) {
}
