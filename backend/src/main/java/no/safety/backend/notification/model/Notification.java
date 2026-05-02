package no.safety.backend.notification.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.User;

/**
 * Represents a notification in the system.
 * Contains message, priority, sender information, and target user.
 * Notifications are created when events are triggered and sent to users via SSE.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false)
    private String message;

    @Column
    private int priority;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private UUID publicId;

    @Column
    private String senderName;

    @Column
    private UUID senderId;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    /**
     * Sets creation timestamp and generates a public UUID before persisting.
     */
    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.publicId = UUID.randomUUID();
    }
}
