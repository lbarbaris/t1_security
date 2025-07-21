package org.example.t1_security.repository;

import org.example.t1_security.model.Role;
import org.example.t1_security.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);

}

