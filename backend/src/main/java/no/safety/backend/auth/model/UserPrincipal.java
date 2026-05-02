package no.safety.backend.auth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import no.safety.backend.user.model.User;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A security wrapper (Principal) that decouples the User entity from Spring Security.
 */
public record UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) implements UserDetails {
    
    /**
     * Creates a UserPrincipal from a User entity.
     * Converts user roles to Spring Security GrantedAuthority objects.
     * @param user the User entity to convert
     * @return a UserPrincipal instance wrapping the user and their authorities
     */
    public static UserPrincipal fromEntity(User user) {
        Set<SimpleGrantedAuthority> authorities = java.util.Optional.ofNullable(user.getRoles())
                .orElse(java.util.Collections.emptySet())
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getType().name()))
                .collect(Collectors.toUnmodifiableSet());

        return new UserPrincipal(user, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true;}

    /**
     * Returns the {@link User} internal ID (primary key).
     * This should be used for database lookups.
     * @return the internal ID of the wrapped User entity
     */
    public Long getUserInternalId() { return user.getInternalId(); }

    /**
     * Returns the {@link User} public UUID.
     * @return the public UUID of the wrapped User entity
     */
    public UUID getUserPublicUuid() { return user.getPublicUuid(); } 
    
}
