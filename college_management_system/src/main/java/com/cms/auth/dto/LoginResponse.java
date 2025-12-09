package com.cms.auth.dto;



import lombok.AllArgsConstructor;

import lombok.Data;



@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Object user;  // dynamic payload (Student / Driver / Admin)
}
