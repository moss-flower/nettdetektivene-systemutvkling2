package no.safety.backend.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation.
 * Sets up API info, contact details, and JWT cookie security scheme.
 */
@Configuration
public class OpenApiConfig {

    @Value("${application.security.jwt.cookie.access.name}")
    private String jwtCookieName;

    /**
     * Creates and configures the OpenAPI bean for API documentation.
     * Sets up API title, version, description, contact info, and JWT cookie security.
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Nettdetektivene API")
                .version("1.0")
                .description("This is the official API documentation for Nettdetektivene - idatt2106_2026_02.")
                .contact(new Contact()
                    .name("")
                    .email("")))
            .addSecurityItem(new SecurityRequirement().addList(jwtCookieName))
            .components(new Components()
                .addSecuritySchemes(jwtCookieName,
                    new SecurityScheme()
                        .name(jwtCookieName)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .description("We use JWT security tokens stored in a HTTP-Only cookie.")));
    }
}
