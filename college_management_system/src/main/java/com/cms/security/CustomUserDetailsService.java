package com.cms.security;


import com.cms.common.enums.Status;
import com.cms.common.repositories.UserRepository;

import jakarta.transaction.Transactional;

import com.cms.college.models.User;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections
                		.singleton(authority))   // set role as authority
                .disabled(user.getStatus() == Status.INACTIVE)   // disable if inactive
                .build();
    }
}
