package com.cms.common;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cms.college.models.Contact;
import com.cms.college.models.User;
import com.cms.common.enums.RoleEnum;
import com.cms.common.enums.Status;
import com.cms.common.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------- CREATE A NEW USER ----------------
    public User createUser(String username, RoleEnum role, Long referenceId, String defaultPassword, Contact contact) {
        log.info("Creating user with username: {}, role: {}, referenceId: {}", username, role, referenceId);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(defaultPassword))
                .referenceId(referenceId)
                .status(Status.ACTIVE)  // default status
                .role(role)             // enum role
                .contact(contact)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with userId: {}", savedUser.getUserId());
        return savedUser;
    }

    // ---------------- UPDATE EXISTING USER ----------------
    public User updateUser(User user) {
        log.info("Updating user with userId: {}", user.getUserId());

        User existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + user.getUserId()));

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            existingUser.setUsername(user.getUsername());
            log.debug("Updated username to: {}", user.getUsername());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Password updated for userId: {}", user.getUserId());
        }

        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
            log.debug("Status updated to: {} for userId: {}", user.getStatus(), user.getUserId());
        }

        if (user.getRole() != null) {
            existingUser.setRole(user.getRole()); // now enum
            log.debug("Role updated to: {} for userId: {}", user.getRole(), user.getUserId());
        }

        if (user.getContact() != null) {
            if (existingUser.getContact() != null) {
                if (user.getContact().getEmail() != null) existingUser.getContact().setEmail(user.getContact().getEmail());
                if (user.getContact().getPhone() != null) existingUser.getContact().setPhone(user.getContact().getPhone());
                if (user.getContact().getAddress() != null) existingUser.getContact().setAddress(user.getContact().getAddress());
                log.debug("Contact updated for userId: {}", user.getUserId());
            } else {
                existingUser.setContact(user.getContact());
                log.debug("Contact set for userId: {}", user.getUserId());
            }
        }

        User savedUser = userRepository.save(existingUser);
        log.info("User updated successfully with userId: {}", savedUser.getUserId());
        return savedUser;
    }

    // ---------------- FETCH USER BY USERNAME ----------------
    public User getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // ---------------- FETCH USER BY ID ----------------
    public User getUserById(Long userId) {
        log.info("Fetching user by userId: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
