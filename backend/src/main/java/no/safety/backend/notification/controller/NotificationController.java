package no.safety.backend.notification.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.notification.service.SseRegistry;

/**
 * Handles incoming connections for establishing Event and Notification systems.
 * Passes user information to service layer.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SseRegistry sseRegistry;

    /**
     * Endpoint for establishing an SSE Emitter connection (one way server events).
     * @param userPrincipal an Authenticated user object that has passed through the JWT filter.
     * @return an SseEmitter for maintaining a one-way connection from server to client. Recieve with Vue EventSource
     */
    @Operation(
        summary = "Create a new Unidirectional connection from the server to client",
        description = "Creates a new connection mapped to the caller. Connection lives until closed.",
        tags = {"Notification management for Teachers"}
    )
    @GetMapping(value = "/stream")
    public SseEmitter stream(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return sseRegistry.register(userPrincipal.user().getInternalId());
    }



}
