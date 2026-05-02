package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import org.springframework.core.io.Resource;

/**
 * Represents a data transfer object for handling submission response
 * for weekly mystery
 */
public record WeeklyMysterySubmissionResponse(
    UUID publicUid,
    String message,
    UUID studentPublicUid,
    Resource file
) {

}
