package no.safety.backend.media.dto;

public record MediaUploadResponse(
	String objectKey,
	String url,
	String contentType,
	long size
) {
}
