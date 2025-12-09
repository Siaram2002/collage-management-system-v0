package com.cms.students.mappers;


import com.cms.students.dto.StudentDTO;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.models.Student;
import com.cms.college.models.Contact;
import com.cms.college.models.Department;
import com.cms.college.models.Course;
import com.cms.college.models.Address;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StudentMapper {

    /**
     * Convert StudentDTO → Student entity (with Contact + Address)
     */
    public static Student toStudent(StudentDTO dto, Department dept, Course course) {

        // Build Address
        Address address = Address.builder()
                .line1(dto.getAddressLine1())
                .line2(dto.getAddressLine2())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .state(dto.getState())
                .country(dto.getCountry())
                .pin(dto.getPin())
                .build();

        // Build Contact
        Contact contact = Contact.builder()
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .altPhone(dto.getAltPhone())
                .address(address)
                .build();

        // Build Student
        return Student.builder()
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .contact(contact)
                .department(dept)
                .course(course)
                .admissionYear(dto.getAdmissionYear())
                .admissionNumber(dto.getAdmissionNumber())
                .rollNumber(dto.getRollNumber())
                .bloodGroup(dto.getBloodGroup())
                .aadhaarNumber(dto.getAdhaarNumber())
                .category(dto.getCategory())
                .nationality(dto.getNationality())
                .status(StudentStatus.ACTIVE)             // <--- set default
                .enrollmentStatus(EnrollmentStatus.ENROLLED) // <--- set default
                .build();


    }
}
