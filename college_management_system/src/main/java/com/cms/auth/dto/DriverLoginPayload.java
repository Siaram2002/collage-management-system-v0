package com.cms.auth.dto;



import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverLoginPayload {

    private Long driverId;

    private String fullName;

    private String licenseNumber;

    private String licenseExpiryDate;

    private String status;

    private String email;

    private String phone;

    private String photoUrl;


    private String role; // e.g., DRIVER
}
