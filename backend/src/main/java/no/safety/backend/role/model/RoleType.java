package no.safety.backend.role.model;

/**
 * Enumeration of the distinct authorization levels within the system.
 * * <p>Used by Spring Security to enforce Role-Based Access Control (RBAC). 
 * These constants map directly to the {@link Role} entity.</p>
 */
public enum RoleType {
    ROLE_STUDENT,
    ROLE_TEACHER,
    ROLE_ADMIN
}
