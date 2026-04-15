package no.safety.backend.role.config;

import lombok.RequiredArgsConstructor;
import no.safety.backend.role.model.Role;
import no.safety.backend.role.model.RoleType;
import no.safety.backend.role.repository.RoleRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * A spring boot ApplicationRunner that initializes the Role table in the database with initial roles.
 */
@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    /**
     * Override for the ApplicationRunner run method.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        populateRoleTableWithAllRoleTypeRoles();
    }

    /**
     * Populates the role table in the database with all roles defined in the RoleType enum.
     * Used to populate the table with all roles defined in the system.
     */
    private void populateRoleTableWithAllRoleTypeRoles(){
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (!roleRepository.existsByType(roleType)) {
                Role role = new Role();
                role.setType(roleType);
                roleRepository.save(role);
            }
        });
    }
}
