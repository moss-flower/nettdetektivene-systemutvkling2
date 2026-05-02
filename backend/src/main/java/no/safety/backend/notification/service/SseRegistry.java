package no.safety.backend.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import no.safety.backend.notification.dto.NotificationDto;
import no.safety.backend.notification.model.Notification;

/**
 * Registry for managing SSE emitters mapped to user IDs.
 * Handles registration, sending notifications, and cleanup of emitters.
 */
@Service
public class SseRegistry {
    private final Logger logger = LoggerFactory.getLogger(SseRegistry.class);
    private Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Registers a new SSE emitter for the specified user.
     * The emitter will be automatically removed on completion, error, or timeout.
     *
     * @param userId the internal ID of the user to register the emitter for
     * @return a new SseEmitter with no timeout
     */
    public SseEmitter register(Long userId) {
        logger.info("JARJAR: SSE Register reached");
        logger.debug("Registering emitter for userId " + userId);
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(userId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onError((ex) -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            logger.error("Failed to send initial ping", e);
        }

        return emitter;
    }

    /**
     * Sends a notification to the specified user via their registered SSE emitter.
     *
     * @param userId the internal ID of the target user
     * @param payload the notification to send
     */
    public void send(Long userId, Notification payload) {
        logger.debug("Sending message to user with internal id: " + userId);

        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                synchronized(emitter) {
                    emitter.send(NotificationDto.fromEntity(payload));
                }
            } catch (IOException e) {
                logger.error("Registry: Emitter not found: " + e );
                emitters.remove(userId);
            }
        }
    }

}
