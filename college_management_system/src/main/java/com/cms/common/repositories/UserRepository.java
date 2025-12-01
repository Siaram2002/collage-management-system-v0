package com.cms.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cms.college.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if username exists (to generate unique username)
    boolean existsByUsername(String username);

}
