package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a data transfer object for handling submission data
 * for weekly mystery
 */
public record WeeklyMysterySubmissionCreateRequest(
    @NotNull(message = "Screenshot required")
    MultipartFile file, 

    @NotBlank(message = "Message required")
    String message,

    @NotNull(message = "Public ID of Weekly Mystery Prompt required")
    UUID weeklyMysterPromptUuid
) {
    
}
