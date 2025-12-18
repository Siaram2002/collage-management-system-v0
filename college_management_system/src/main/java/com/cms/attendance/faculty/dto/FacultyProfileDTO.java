package com.cms.attendance.faculty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyProfileDTO {

    // ---------- Faculty Core ----------
    private Long facultyId;
    private String facultyCode;
    private String fullName;
//    private LocalDate dob;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String designation;
    private String status;

    // ---------- Department ----------
    private Long departmentId;

    // ---------- Contact ----------
    private String email;
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

    // ---------- Media ----------
    private String photoUrl;
}
