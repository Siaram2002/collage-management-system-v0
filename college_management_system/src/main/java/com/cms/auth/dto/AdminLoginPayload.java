package com.cms.auth.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLoginPayload {

    private Long adminId;

    private String fullName;

    private String email;

    private String phone;

    private String photoUrl;

    private String status;


    private String role;  // e.g., ADMIN
}
