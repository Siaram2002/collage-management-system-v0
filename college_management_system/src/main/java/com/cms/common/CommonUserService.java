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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 1️⃣ Create a new user
    public User createUser(String username, String roleName, Long referenceId, String defaultPassword, Contact contact) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(defaultPassword))
                .referenceId(referenceId)
                .status(Status.INACTIVE)
                .roles(Set.of(role))
                .contact(contact)
                .build();

        return userRepository.save(user);
    }

    // 2️⃣ Update an existing user
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + user.getUserId()));

        if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() != null) existingUser.setStatus(user.getStatus());
        if (user.getRoles() != null && !user.getRoles().isEmpty()) existingUser.setRoles(user.getRoles());

        if (user.getContact() != null) {
            if (existingUser.getContact() != null) {
                existingUser.getContact().setEmail(user.getContact().getEmail());
                existingUser.getContact().setPhone(user.getContact().getPhone());
                existingUser.getContact().setAddress(user.getContact().getAddress());
            } else {
                existingUser.setContact(user.getContact());
            }
        }

        return userRepository.save(existingUser);
    }

    // 3️⃣ Fetch user by username (returns null if not found)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
