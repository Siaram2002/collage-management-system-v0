package com.cms.students.services;

import com.cms.students.dto.StudentUploadDTO;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;

import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.reporitories.CourseRepository;
import com.cms.college.reporitories.DepartmentRepository;

import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;

import com.cms.students.services.media.PhotoStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentBulkUploadService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final CommonUserService commonUserService;
    private final PhotoStorageService photoStorageService;

    private static final int THREAD_POOL_SIZE = 10;
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    /**
     * MAIN BULK UPLOAD
     */
 
    public void uploadStudentsBulk(MultipartFile file, String imagesFolderPath) throws IOException {

        log.info("Received bulk student upload");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                StudentUploadDTO dto = parseRow(row);
                if (dto.getRollNumber() == null || dto.getRollNumber().isBlank()) continue;

                try {
                    Student saved = saveStudentWithoutPhoto(dto);

                    executor.submit(() ->
                            uploadPhotoAndUpdate(saved.getStudentId(), dto, imagesFolderPath)
                    );

                } catch (Exception e) {
                    log.error("❌ Failed at row {} roll={} : {}", i + 1, dto.getRollNumber(), e.getMessage(), e);
                }
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {}

        log.info("Bulk upload completed.");
    }

    /**
     * SAVE STUDENT + USER (NO PHOTO)
     * CLEAN — single transaction
     */
    @Transactional
    public Student saveStudentWithoutPhoto(StudentUploadDTO dto) {

        Contact contact = buildContact(dto);

        var dept = departmentRepository.findByDepartmentCode(dto.getDepartmentCode())
                .orElseThrow();
        var course = courseRepository.findByCourseCode(dto.getCourseCode())
                .orElseThrow();

        Optional<Student> existing = studentRepository.findByRollNumber(dto.getRollNumber());
        Student st;

        if (existing.isPresent()) {
            st = existing.get();
            st.setFirstName(dto.getFirstName());
            st.setMiddleName(dto.getMiddleName());
            st.setLastName(dto.getLastName());
            st.setDob(dto.getDob());
            st.setGender(dto.getGender());
            st.setContact(contact);
            st.setDepartment(dept);
            st.setCourse(course);
            st.setAdmissionYear(dto.getAdmissionYear());
            st.setAdmissionNumber(dto.getAdmissionNumber());
            st.setBloodGroup(dto.getBloodGroup());
            st.setCategory(dto.getCategory());
            st.setNationality(dto.getNationality());
            st.setAadhaarNumber(dto.getAdhaarNumber());

        } else {
            st = Student.builder()
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
                    .status(StudentStatus.ACTIVE)
                    .enrollmentStatus(EnrollmentStatus.ENROLLED)
                    .bloodGroup(dto.getBloodGroup())
                    .category(dto.getCategory())
                    .nationality(dto.getNationality())
                    .aadhaarNumber(dto.getAdhaarNumber())
                    .build();
        }

        st = studentRepository.save(st);

        // Create user (VERY IMPORTANT — outside async)
        var user = commonUserService.createUser(
                st.getRollNumber(),
                RoleEnum.STUDENT,
                st.getStudentId(),
                "stu@123",
                st.getContact()
        );

        st.setUser(user);

        return studentRepository.save(st);
    }


    /**
     * ASYNC — READ LOCAL FILE + UPLOAD + UPDATE DB
     */
    @Transactional
    public void uploadPhotoAndUpdate(Long studentId, StudentUploadDTO dto, String imagesFolderPath) {

        try {
            Optional<Student> opt = studentRepository.findById(studentId);
            if (opt.isEmpty()) return;

            Student st = opt.get();

            File folder = new File(imagesFolderPath);
            if (!folder.exists()) return;

            File[] files = folder.listFiles(f ->
                    f.getName().toLowerCase().startsWith(dto.getRollNumber().toLowerCase()) &&
                            (f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))
            );

            if (files == null || files.length == 0) {
                log.warn("⚠️ No image found for {}", dto.getRollNumber());
                return;
            }

            File img = files[0];
            byte[] bytes = Files.readAllBytes(img.toPath());

            String url = photoStorageService.updateStudentPhoto(dto.getRollNumber(), bytes);

            st.setPhotoUrl(url);
            studentRepository.save(st);

            log.info("📸 Photo updated → {} = {}", dto.getRollNumber(), url);

        } catch (Exception e) {
            log.error("❌ Photo update failed for {} : {}", dto.getRollNumber(), e.getMessage());
        }
    }


    /* ------------- HELPERS --------------- */

    private StudentUploadDTO parseRow(Row r) {
        StudentUploadDTO dto = new StudentUploadDTO();

        dto.setFirstName(get(r, 0));
        dto.setMiddleName(get(r, 1));
        dto.setLastName(get(r, 2));
        dto.setDob(parseDate(r.getCell(3)));
        dto.setGender(get(r, 4));
        dto.setPhone(get(r, 5));
        dto.setEmail(get(r, 6));
        dto.setAltPhone(get(r, 7));

        dto.setAddressLine1(get(r, 8));
        dto.setAddressLine2(get(r, 9));
        dto.setCity(get(r, 10));
        dto.setDistrict(get(r, 11));
        dto.setState(get(r, 12));
        dto.setCountry(get(r, 13));
        dto.setPin(get(r, 14));

        dto.setDepartmentCode(get(r, 15));
        dto.setCourseCode(get(r, 16));
        dto.setAdmissionYear(parseInt(get(r, 17)));
        dto.setAdmissionNumber(get(r, 18));
        dto.setRollNumber(get(r, 19));
        dto.setBloodGroup(get(r, 20));
        dto.setCategory(get(r, 21));
        dto.setNationality(get(r, 22));
        dto.setAdhaarNumber(get(r, 23));

        return dto;
    }

    private Contact buildContact(StudentUploadDTO dto) {
        Address address = Address.builder()
                .line1(dto.getAddressLine1())
                .line2(dto.getAddressLine2())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .state(dto.getState())
                .country(dto.getCountry())
                .pin(dto.getPin())
                .build();

        return Contact.builder()
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .altPhone(dto.getAltPhone())
                .address(address)
                .build();
    }

    private String get(Row r, int c) {
        Cell cell = r.getCell(c);
        return cell == null ? "" : cell.toString().trim();
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            String str = cell.getStringCellValue().trim();
            for (DateTimeFormatter fmt : DATE_FORMATS) {
                try { return LocalDate.parse(str, fmt); }
                catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        return null;
    }

    private int parseInt(String s) {
        if (s == null || s.isBlank()) return 0;
        if (s.contains(".")) s = s.substring(0, s.indexOf('.'));
        return Integer.parseInt(s);
    }
}
