package no.safety.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Update request for changing the password of a {@link User}.
 */
public record PasswordUpdateRequest(
    @NotBlank(message = "Password is required for this request.")
    @Size(min = 2, max = 50, message = "Password must be between 2 and 50 characters.")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$", 
        message = "Please provide a valid password."
    )
    String password
) {}