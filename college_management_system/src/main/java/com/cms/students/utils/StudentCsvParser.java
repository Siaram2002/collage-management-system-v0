package com.cms.students.utils;

import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.students.models.Student;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class StudentCsvParser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parse CSV and create Student list
     */
    public static List<Student> parseStudents(
            InputStream csvInputStream,
            Map<String, Department> departmentCodeMap,
            Map<String, Course> courseCodeMap
    ) throws IOException {

        List<Student> students = new ArrayList<>();

        try (Reader reader = new InputStreamReader(csvInputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .withTrim())) {

            for (CSVRecord record : csvParser) {

                String firstName = record.get("firstName");
                String middleName = record.get("middleName");
                String lastName = record.get("lastName");
                LocalDate dob = LocalDate.parse(record.get("dob"), DATE_FORMAT);
                String gender = record.get("gender");
                Integer admissionYear = Integer.parseInt(record.get("admissionYear"));
                String admissionNumber = record.get("admissionNumber");
                String rollNumber = record.get("rollNumber");

                // Map department and course by code
                String deptCode = record.get("departmentCode");
                Department department = departmentCodeMap.get(deptCode);
                if (department == null) {
                    throw new IllegalArgumentException("Invalid department code: " + deptCode);
                }

                String courseCode = record.get("courseCode");
                Course course = courseCodeMap.get(courseCode);
                if (course == null) {
                    throw new IllegalArgumentException("Invalid course code: " + courseCode);
                }

                // Contact & Address
                Address address = Address.builder()
                        .line1(record.get("addressLine1"))
                        .line2(record.get("addressLine2"))
                        .city(record.get("city"))
                        .district(record.get("district"))
                        .state(record.get("state"))
                        .country(record.get("country"))
                        .pin(record.get("pin"))
                        .build();

                Contact contact = Contact.builder()
                        .phone(record.get("phone"))
                        .altPhone(record.get("altPhone"))
                        .email(record.get("email"))
                        .address(address)
                        .build();

                // Optional fields
                String bloodGroup = record.isMapped("bloodGroup") ? record.get("bloodGroup") : null;
                String category = record.isMapped("category") ? record.get("category") : null;
                String nationality = record.isMapped("nationality") ? record.get("nationality") : null;

                // Build student
                Student student = Student.builder()
                        .firstName(firstName)
                        .middleName(middleName)
                        .lastName(lastName)
                        .dob(dob)
                        .gender(gender)
                        .admissionYear(admissionYear)
                        .admissionNumber(admissionNumber)
                        .rollNumber(rollNumber)
                        .department(department)
                        .course(course)
                        .contact(contact)
                        .status(StudentStatus.ACTIVE)
                        .enrollmentStatus(EnrollmentStatus.ENROLLED)
                        .bloodGroup(bloodGroup)
                        .category(category)
                        .nationality(nationality)
                        .build();

                students.add(student);
            }
        }

        log.info("Parsed {} students from CSV", students.size());
        return students;
    }
}
