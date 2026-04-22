package no.safety.backend.notification.repository;

import java.util.Set;

import javax.management.Notification;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Defines a JPA repository for Notifications
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Set<Notification> findByTargets_IntenalId(long id);
} 
