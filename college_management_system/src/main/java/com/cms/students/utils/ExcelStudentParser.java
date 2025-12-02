package com.cms.students.utils;

import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.students.models.Student;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelStudentParser {

    public static List<Student> parseStudents(InputStream inputStream,
                                              Map<String, Department> departmentMap,
                                              Map<String, Course> courseMap) throws Exception {
        List<Student> students = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // --- Student fields ---
            String firstName = getCellString(row.getCell(0));
            String middleName = getCellString(row.getCell(1));
            String lastName = getCellString(row.getCell(2));
            LocalDate dob = LocalDate.parse(getCellString(row.getCell(3)), formatter);
            String gender = getCellString(row.getCell(4));
            String rollNumber = getCellString(row.getCell(5));
            String admissionNumber = getCellString(row.getCell(6));
            Integer admissionYear = (int) row.getCell(7).getNumericCellValue();
            String departmentCode = getCellString(row.getCell(8));
            String courseCode = getCellString(row.getCell(9));

            Department department = departmentMap.get(departmentCode);
            Course course = courseMap.get(courseCode);

            // --- Contact fields ---
            String phone = getCellString(row.getCell(10));
            String email = getCellString(row.getCell(11));
            String altPhone = getCellString(row.getCell(12));

            // --- Address fields ---
            String line1 = getCellString(row.getCell(13));
            String line2 = getCellString(row.getCell(14));
            String city = getCellString(row.getCell(15));
            String district = getCellString(row.getCell(16));
            String state = getCellString(row.getCell(17));
            String country = getCellString(row.getCell(18));
            String pin = getCellString(row.getCell(19));

            Address address = Address.builder()
                    .line1(line1)
                    .line2(line2)
                    .city(city)
                    .district(district)
                    .state(state)
                    .country(country)
                    .pin(pin)
                    .build();

            Contact contact = Contact.builder()
                    .phone(phone)
                    .email(email)
                    .altPhone(altPhone.isEmpty() ? null : altPhone)
                    .address(address)
                    .build();

            Student student = Student.builder()
                    .firstName(firstName)
                    .middleName(middleName.isEmpty() ? null : middleName)
                    .lastName(lastName.isEmpty() ? null : lastName)
                    .dob(dob)
                    .gender(gender)
                    .rollNumber(rollNumber)
                    .admissionNumber(admissionNumber)
                    .admissionYear(admissionYear)
                    .department(department)
                    .course(course)
                    .contact(contact)
                    .status(com.cms.students.enums.StudentStatus.ACTIVE)
                    .enrollmentStatus(com.cms.students.enums.EnrollmentStatus.ENROLLED)
                    .build();

            students.add(student);
        }

        workbook.close();
        return students;
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
