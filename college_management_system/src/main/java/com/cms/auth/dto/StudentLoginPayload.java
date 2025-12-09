package com.cms.auth.dto;



import lombok.AllArgsConstructor;
import lombok.Data;



import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLoginPayload {

    private Long studentId;

    private String firstName;

    private String lastName;

    private String rollNumber;

    private String admissionNumber;

    private String courseName;

    private String departmentName;

    private String photoUrl;

    private String email;
    
    private String bloodGroup;

    private String phone;

    private String role;   // e.g., STUDENT
}
