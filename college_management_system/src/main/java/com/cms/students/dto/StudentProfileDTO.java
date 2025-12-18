package com.cms.students.dto;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {



    // ---------------- Basic Info ----------------
    private Long studentId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String bloodGroup;
    private String category;
    private String nationality;
    private String adhaarNumber;

    // ---------------- Contact Info ----------------
    private String contactPhone;
    private String contactEmail;
    private String altPhone;

    // Address details (optional)
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pin;
    private Double latitude;
    private Double longitude;

    // ---------------- Department Info ----------------
   
    private String departmentName;
    private String departmentCode;

  

    // ---------------- Course Info ----------------
  
    private String courseName;
    private String courseCode;



    // ---------------- Student Academic Info ----------------
    private Integer admissionYear;
    private String admissionNumber;
    private String rollNumber;
    private StudentStatus status;
    private EnrollmentStatus enrollmentStatus;

    // ---------------- Media Info ----------------
    private String photoUrl;
    private String qrCodeUrl;

}
