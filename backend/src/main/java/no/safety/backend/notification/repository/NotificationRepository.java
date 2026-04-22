package no.safety.backend.notification.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import no.safety.backend.notification.model.Notification;

/**
 * Defines a JPA repository for Notifications
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Set<Notification> findByTarget_IntenalId(long id);
} 
