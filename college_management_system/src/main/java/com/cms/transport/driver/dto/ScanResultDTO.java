package com.cms.transport.driver.dto;



import com.cms.students.enums.StudentStatus;
import lombok.Data;

@Data
public class ScanResultDTO {

    // ------------------------------
    // StudentBasic Details (from StudentResponse)
    // ------------------------------
    private String firstName;
    private String middleName;
    private String lastName;

    private String rollNumber;
    private String admissionNumber;
    private Integer admissionYear;

    private String department;
    private String course;

    private StudentStatus status;

    // ------------------------------
    // Photo
    // ------------------------------
    private String photoUrl;

    // ------------------------------
    // Payment Status Section
    // ------------------------------
    private boolean feePaid;          // true/false

}

