package no.safety.backend.weeklymystery.dto;

import java.util.UUID;

import org.springframework.core.io.Resource;

public record WeeklyMysterySubmissionResponse(
    UUID publicUid,
    String message,
    UUID studentPublicUid,
    Resource file
) {

}
