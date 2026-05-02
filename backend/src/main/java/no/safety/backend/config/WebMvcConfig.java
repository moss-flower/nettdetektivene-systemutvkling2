package no.safety.backend.config;

import no.safety.backend.logging.RequestContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global web configuration for the application.
 * <p>
 * This class registers custom interceptors that must execute during the 
 * HTTP request lifecycle. It ensures cross-cutting concerns, such as 
 * MDC (Mapped Diagnostic Context) logging, are applied uniformly across 
 * all endpoints.
 * </p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestContextInterceptor requestContextInterceptor;

    /**
     * Constructs the web configuration with required interceptors.
     *
     * @param requestContextInterceptor the interceptor responsible for injecting 
     *                                  unique request IDs into the logging MDC
     */
    public WebMvcConfig(RequestContextInterceptor requestContextInterceptor) {
        this.requestContextInterceptor = requestContextInterceptor;
    }

    /**
     * Registers interceptors with the Spring MVC registry.
     * <p>
     * The RequestContextInterceptor is mapped to {@code /**} to guarantee 
     * that every single request, regardless of the path, is assigned a 
     * traceability ID before reaching the controller layer.
     * </p>
     *
     * @param registry the Spring interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestContextInterceptor)
                .addPathPatterns("/**"); // Applies to all URLs
    }
}