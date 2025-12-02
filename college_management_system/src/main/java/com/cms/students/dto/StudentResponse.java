package com.cms.students.dto;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import lombok.Data;

@Data
public class StudentResponse {
    private Long studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String rollNumber;
    private String admissionNumber;
    private Integer admissionYear;
    private String department;
    private String course;
    private StudentStatus status;
    private EnrollmentStatus enrollmentStatus;

    // Newly added
    private String photoUrl;
    private String qrUrl;
}
