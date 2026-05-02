package no.safety.backend.notification.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.notification.model.Notification;

/**
 * Repository for managing notification persistence.
 * Provides methods for finding notifications by target user and public ID.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Set<Notification> findByTarget_InternalId(long id);

    Optional<Notification> findByPublicId(UUID publicId);
} 
