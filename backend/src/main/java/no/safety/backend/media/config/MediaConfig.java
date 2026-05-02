package no.safety.backend.media.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MediaProperties.class)
public class MediaConfig {

	@Bean
	public MinioClient minioClient(MediaProperties properties) {
		return MinioClient.builder()
			.endpoint(properties.getEndpoint())
			.credentials(properties.getAccessKey(), properties.getSecretKey())
			.build();
	}
}
