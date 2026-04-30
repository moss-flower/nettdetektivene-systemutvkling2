package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a data transfer object for handling submission data
 * for weekly mystery
 */
public record WeeklyMysterySubmissionCreateRequest(
    @NotBlank(message = "Screenshot Required")
    @Size(max = 1, message = "Please only attach a single image.")
    MultipartFile file, 

    @NotBlank(message = "Message Required")
    String message,

    @NotBlank(message = "Public ID of Weekly Mystery Prompt required")
    UUID weeklyMysterPromptUuid
) {
    
}
