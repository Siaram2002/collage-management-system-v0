package com.cms.attendance.faculty.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRequestDTO {

    // ---------- Faculty Core ----------
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Date of birth is required")
    private String dob; // yyyy-MM-dd

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Status is required")
    private String status; // ACTIVE / INACTIVE

    // ---------- Department ----------
    private Long departmentId;

    // ---------- Contact ----------
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    private String phone;

    private String altPhone;

    // ---------- Address ----------
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pin;
    private Double latitude;
    private Double longitude;
}
