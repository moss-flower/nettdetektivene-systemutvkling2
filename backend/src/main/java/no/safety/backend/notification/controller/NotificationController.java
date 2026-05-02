package no.safety.backend.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.notification.service.NotificationService;
import no.safety.backend.notification.service.SseRegistry;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for notification endpoints.
 * Provides endpoints for establishing SSE connections and managing
 * notifications.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final Logger logger = LoggerFactory.getLogger(
            NotificationController.class);
    private final SseRegistry sseRegistry;
    private final NotificationService notificationService;

    /**
     * Establishes an SSE (Server-Sent Events) connection for the authenticated
     * user.
     * Flushes any pending notifications to the client upon connection.
     *
     * @param userPrincipal the authenticated user principal
     * @return an SseEmitter for maintaining a one-way connection from server to
     *         client
     */
    @Operation(
        summary = "Create a new Unidirectional connection from the server to client",
        description = "Creates a new connection mapped to the caller. Connection lives until closed.",
        tags = { "Notification management for Teachers" }
    )
    @GetMapping(value = "/sse")
    public SseEmitter stream(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        logger.info("Controller - principal: " + userPrincipal);
        logger.info(
                "Controller - context auth: " +
                        SecurityContextHolder.getContext().getAuthentication());
        logger.info("Controller hit, principal: " + userPrincipal);
        Long userId = userPrincipal.getUserInternalId();
        SseEmitter emitter = sseRegistry.register(userId);

        notificationService.flushPending(userId);
        System.out.println("Responding with emitter " + emitter);
        return emitter;
    }

    /**
     * Deletes a notification identified by its UUID.
     *
     * @param notificationUuid the UUID of the notification to delete
     * @param principal        the authenticated user principal
     * @return a response with no content on successful deletion
     */
    @DeleteMapping("/{notificationUuid}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationUuid,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.deleteNotification(notificationUuid, principal.getUserInternalId());
        return ResponseEntity.noContent().build();
    }

}
