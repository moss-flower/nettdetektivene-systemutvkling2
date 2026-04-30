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

@Configuration
public class OpenApiConfig {

    @Value("${application.security.jwt.cookie.access.name}")
    private String jwtCookieName;

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
