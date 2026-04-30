package no.safety.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import no.safety.backend.validation.NoProfanity;

/**
 * Update request for changing the username of a {@link User}.
 */
public record UsernameUpdateRequest(
    @NotBlank(message = "Username is required for this request.")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
    @NoProfanity(message = "Username contains inappropriate language.")
    String username
) {}
