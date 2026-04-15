package no.safety.backend.user.repository;

import no.safety.backend.user.model.Role;
import no.safety.backend.user.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByType(RoleType type);
    Boolean existsByType(RoleType type);
}