package no.safety.backend.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor that manages the Mapped Diagnostic Context (MDC) for HTTP
 * requests.
 * <p>
 * Generates a unique trace ID for each incoming request to enable log
 * correlation.
 * Crucially, it ensures the MDC is cleared after request completion to prevent
 * context bleeding and memory leaks within servlet container thread pools.
 */
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    /**
     * Injects a unique {@code requestId} into the MDC before request processing.
     * <p>
     * Note: Sensitive user details must never be added to the MDC unless properly
     * masked.
     *
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param handler  the chosen handler to execute
     * @return {@code true} to proceed with the execution chain
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        return true;
    }

    /**
     * Clears the MDC context after the request lifecycle completes.
     * <p>
     * This step is mandatory in thread-pooled environments (like Tomcat) to
     * guarantee that lingering MDC data does not bleed into subsequent requests.
     *
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param handler  the executed handler
     * @param ex       any exception thrown during handler execution
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        MDC.clear(); 
    }
}