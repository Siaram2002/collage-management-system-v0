package com.cms.common;

import java.util.Set;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cms.college.models.Contact;
import com.cms.college.models.Role;
import com.cms.college.models.User;
import com.cms.common.enums.Status;
import com.cms.common.repositories.RoleRepository;
import com.cms.common.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;   // ⬅ Needed to fetch Role entity
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String roleName, Long referenceId, String defaultPassword, Contact contact) {

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(defaultPassword))
                .referenceId(referenceId)
                .status(Status.INACTIVE)
                .roles(Set.of(role))
                .contact(contact)   // <---- link the student’s contact
                .build();

        return userRepository.save(user);
    }

}
