package com.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll() // Allow all requests without authentication
//            )
//            .formLogin(form -> form.disable())   // Disable login form
//            .httpBasic(httpBasic -> httpBasic.disable()); // Disable HTTP Basic auth
//
//        return http.build();
//    }
}
