package com.cms.common.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cms.college.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
