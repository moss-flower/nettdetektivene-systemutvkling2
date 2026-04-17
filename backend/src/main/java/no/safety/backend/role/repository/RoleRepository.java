package no.safety.backend.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;

import java.util.Optional;

/**
 * Repository for managing {@link Role} persistence.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByType(RoleType type);
    Boolean existsByType(RoleType type);
}