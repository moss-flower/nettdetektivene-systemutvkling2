package no.safety.backend.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.validation.NoProfanity;

/**
 * DTO for student user registration requests.
 * Contains the required fields for registering a new student account.
 */
public record StudentUserRegistrationRequest(
    @NotBlank(message = "Username is required.")
    @NoProfanity(message = "Username contains inappropriate language.")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address.")
    String email,

    @NotBlank 
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters long.")
    String password
) {
} 
