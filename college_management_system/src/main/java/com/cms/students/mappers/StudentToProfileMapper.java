package com.cms.students.mappers;

import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.models.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentToProfileMapper {

    public StudentProfileDTO toProfileDTO(Student s) {
        if (s == null) return null;

        return StudentProfileDTO.builder()
                // Basic Info
                .studentId(s.getStudentId())
                .firstName(s.getFirstName())
                .middleName(s.getMiddleName())
                .lastName(s.getLastName())
                .dob(s.getDob() != null ? s.getDob().toString() : null)
                .gender(s.getGender())
                .bloodGroup(s.getBloodGroup())
                .category(s.getCategory())
                .nationality(s.getNationality())
                // Contact
                .contactPhone(s.getContact() != null ? s.getContact().getPhone() : null)
                .contactEmail(s.getContact() != null ? s.getContact().getEmail() : null)
                .altPhone(s.getContact() != null ? s.getContact().getAltPhone() : null)
                // Address
                .addressLine1(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getLine1() : null)
                .addressLine2(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getLine2() : null)
                .city(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getCity() : null)
                .district(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getDistrict() : null)
                .state(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getState() : null)
                .country(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getCountry() : null)
                .pin(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getPin() : null)
                .latitude(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getLatitude() : null)
                .longitude(s.getContact() != null && s.getContact().getAddress() != null ? s.getContact().getAddress().getLongitude() : null)
                // Department
                .departmentName(s.getDepartment() != null ? s.getDepartment().getName() : null)
                .departmentCode(s.getDepartment() != null ? s.getDepartment().getDepartmentCode() : null)
                // Course
                .courseName(s.getCourse() != null ? s.getCourse().getName() : null)
                .courseCode(s.getCourse() != null ? s.getCourse().getCourseCode() : null)
                // Academic Info
                .admissionYear(s.getAdmissionYear())
                .admissionNumber(s.getAdmissionNumber())
                .rollNumber(s.getRollNumber())
                .status(s.getStatus())
                .enrollmentStatus(s.getEnrollmentStatus())
                .adhaarNumber(s.getAadhaarNumber())
                // Media Info
                .photoUrl(s.getPhotoUrl())
                .qrCodeUrl(s.getQrCodeUrl())

                .build();
    }
}
