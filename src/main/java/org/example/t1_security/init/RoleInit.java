package org.example.t1_security.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.t1_security.model.Role;
import org.example.t1_security.model.RoleEnum;
import org.example.t1_security.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInit {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        for (RoleEnum roleName : RoleEnum.values()) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                var role = Role.builder().name(roleName).build();
                return roleRepository.save(role);
            });
        }
    }
}

