package no.safety.backend.notification.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.User;

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

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.publicId = UUID.randomUUID();
    }
}
