package com.cms.auth.dto;



import lombok.Data;

@Data
public class LoginPayload {
    private String token;
    private Long userId;
    private String role;
    private String name;
    private String profileImageUrl;
}
