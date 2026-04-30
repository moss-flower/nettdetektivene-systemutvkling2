package no.safety.backend.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import no.safety.backend.role.model.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Defines a user entity in the system.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {

    /* The internal user id used by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long internalId;

    /* The public UUID for the user sent to the client, should be used in API calls */
    @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID publicUuid;

    @Column(nullable = false, unique = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * Generates a random UUID for the user upon instance creation.
     */
    @PrePersist
    protected void onCreate() {
        if (this.publicUuid == null) {
            this.publicUuid = UUID.randomUUID();
        }
    }

    /**
     * Creates a many to many database relation between users and roles.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    /* The set of roles the user has. */
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Override equals definition to ensure two different objects for the same user are considered equal by the system.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return email != null && email.equals(user.getEmail());
    }

    /**
     * JPA safe hashcode pattern
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}