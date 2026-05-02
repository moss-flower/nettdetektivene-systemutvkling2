package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

public record WeeklyMysterySubmissionResponse(
    UUID publicUid,
    String message,
    UUID studentPublicUid,
    String fileName,
    String fileUrl
) {

}
