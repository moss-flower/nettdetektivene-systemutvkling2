package no.safety.backend.media.service;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.exception.BadRequestException;
import no.safety.backend.media.config.MediaProperties;
import no.safety.backend.media.dto.MediaUploadResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaStorageService {

	private static final long MAX_UPLOAD_BYTES = 10L * 1024L * 1024L;

	private final MinioClient minioClient;
	private final MediaProperties mediaProperties;

	public MediaUploadResponse uploadPublicImage(MultipartFile file, String folder) {
		validateFile(file);

		String objectKey = buildObjectKey(folder, file.getOriginalFilename(), file.getContentType());

		try (InputStream inputStream = file.getInputStream()) {
			uploadPublicObject(objectKey, inputStream, file.getSize(), file.getContentType());
		} catch (Exception ex) {
			throw new IllegalStateException("Could not upload media file.", ex);
		}

		return new MediaUploadResponse(objectKey, publicUrl(objectKey), file.getContentType(), file.getSize());
	}

	public String uploadPublicClasspathImage(String objectKey, String classpathResourcePath, String contentType) {
		ClassPathResource resource = new ClassPathResource(classpathResourcePath);
		if (!resource.exists()) {
			throw new IllegalStateException("Could not find seed media file: " + classpathResourcePath);
		}

		try (InputStream inputStream = resource.getInputStream()) {
			uploadPublicObject(objectKey, inputStream, resource.contentLength(), contentType);
		} catch (Exception ex) {
			throw new IllegalStateException("Could not upload media file.", ex);
		}

		return publicUrl(objectKey);
	}

	public String publicUrl(String objectKey) {
		String normalizedBase = mediaProperties.getPublicBaseUrl().replaceAll("/+$", "");
		String normalizedKey = objectKey.replaceFirst("^/+", "");
		return normalizedBase + "/" + mediaProperties.getPublicBucket() + "/" + normalizedKey;
	}

	public void deletePublicObject(String objectKey) {
		try {
			minioClient.removeObject(
				RemoveObjectArgs.builder()
					.bucket(mediaProperties.getPublicBucket())
					.object(objectKey)
					.build()
			);
		} catch (Exception ex) {
			throw new IllegalStateException("Could not delete media file.", ex);
		}
	}

	private void uploadPublicObject(String objectKey, InputStream inputStream, long size, String contentType)
		throws Exception {
		minioClient.putObject(
			PutObjectArgs.builder()
				.bucket(mediaProperties.getPublicBucket())
				.object(objectKey)
				.stream(inputStream, size, -1)
				.contentType(contentType)
				.build()
		);
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("file is required.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new BadRequestException("Only image uploads are supported.");
		}

		if (file.getSize() > MAX_UPLOAD_BYTES) {
			throw new BadRequestException("Image files cannot exceed 10 MB.");
		}
	}

	private String buildObjectKey(String folder, String originalFilename, String contentType) {
		String normalizedFolder = normalizeFolder(folder);
		String extension = resolveExtension(originalFilename, contentType);
		return normalizedFolder + UUID.randomUUID() + extension;
	}

	private String normalizeFolder(String folder) {
		if (folder == null || folder.isBlank()) {
			return "uploads/general/";
		}

		String normalized = folder.trim().toLowerCase(Locale.ROOT).replace('\\', '/');
		normalized = normalized.replaceAll("[^a-z0-9/_-]", "-");
		normalized = normalized.replaceAll("/{2,}", "/");
		normalized = normalized.replaceAll("^/+", "");
		normalized = normalized.replaceAll("/+$", "");

		if (normalized.isBlank()) {
			return "uploads/general/";
		}

		return normalized + "/";
	}

	private String resolveExtension(String originalFilename, String contentType) {
		if (originalFilename != null) {
			int extensionIndex = originalFilename.lastIndexOf('.');
			if (extensionIndex >= 0) {
				String extension = originalFilename.substring(extensionIndex).toLowerCase(Locale.ROOT);
				if (extension.matches("\\.(png|jpg|jpeg|webp|gif|svg)")) {
					return extension;
				}
			}
		}

		return switch (contentType) {
			case "image/png" -> ".png";
			case "image/jpeg" -> ".jpg";
			case "image/webp" -> ".webp";
			case "image/gif" -> ".gif";
			case "image/svg+xml" -> ".svg";
			default -> throw new BadRequestException("Unsupported image type.");
		};
	}
}
