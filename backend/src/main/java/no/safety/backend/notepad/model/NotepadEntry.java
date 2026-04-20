package no.safety.backend.notepad.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.user.model.User;

/**
 * Defines a notepadentry in the system.
 */
@Entity
@Table(name = "notepad_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotepadEntry {
    /* The internal entity id used by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long internalId;

    /* The public UUID for the entity sent to the client, should be used in API calls */
    @Column(updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID publicUuid;

    /* The game that this note belongs to.  */
    @NotNull
    @Column(name = "game_id")
    private Long gameId;

    /* The title of the entry. */
    @NotBlank
    @Size(max = 128)
    @Column(nullable = false, length = 128)
    private String title;

    /* The date the entry was created */
    @NotNull
    @Column(nullable = false)
    private LocalDate dateCreated;

    /* The source of the entry. */
    @Size(max = 128)
    private String source;

    /* The type of entry. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private NotepadEntryType type;

    /* The content of the entry. */
    @NotBlank
    @Size(max = 256)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Creates a many to many database relation between notepad entries and users.
     * Allows multiple users to have access to the same note. e.g. student and teacher.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "notepad_entry_users",
            joinColumns = @JoinColumn(name = "notepad_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private Set<User> users = new java.util.HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.publicUuid == null) {
            this.publicUuid = UUID.randomUUID();
        }
        if (this.dateCreated == null) {
            this.dateCreated = LocalDate.now();
        }
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    /**
     * Equality based on publicUUID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotepadEntry that)) return false;
        return publicUuid != null && publicUuid.equals(that.publicUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
