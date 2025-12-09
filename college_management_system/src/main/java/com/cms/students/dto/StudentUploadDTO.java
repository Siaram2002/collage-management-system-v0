package com.cms.students.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class StudentUploadDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dob;
    private String gender;

    private String phone;
    private String email;
    private String altPhone;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pin;

    private String departmentCode;
    private String courseCode;

    
    private Integer admissionYear;
    private String admissionNumber;
    private String rollNumber;

    private String bloodGroup;
    private String category;
    private String nationality;
    private String adhaarNumber;
}
