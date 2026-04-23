package no.safety.backend.notification.service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseRegistry {
    private final Logger logger = LoggerFactory.getLogger(SseRegistry.class);
    private Map<Long, SseEmitter> emitters;

    public SseEmitter register(Long userId) {
        logger.debug("Registering emitter for userId " + userId);
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(userId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onError((ex) -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    public void send(Long userId, Object payload) {
        logger.debug("Sending message to user with internal id: " + userId);
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                synchronized(emitter) {
                    emitter.send(payload);
                }
            } catch (IOException e) {
                logger.error("Registry: Emitter not found: " + e );
                emitters.remove(userId);
            }
        }
    }

}
