package no.safety.backend.media.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.safety.backend.media.dto.MediaUploadResponse;
import no.safety.backend.media.service.MediaStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

	private final MediaStorageService mediaStorageService;

	@Operation(summary = "Upload a public image", tags = {"Media"})
	@PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
	@PostMapping(value = "/uploads/public", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public MediaUploadResponse uploadPublicImage(
		@RequestParam("file") MultipartFile file,
		@RequestParam(value = "folder", required = false) String folder
	) {
		return mediaStorageService.uploadPublicImage(file, folder);
	}
}
