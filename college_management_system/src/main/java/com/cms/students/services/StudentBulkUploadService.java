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

import com.cms.students.services.media.QRFileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
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
    private final PhotoStorageService photoStorageService;
  
    private final QRFileStorageService qrFileStorageService;

    private final CommonUserService commonUserService;

    private static final int BATCH_SIZE = 500; // batch size for DB inserts
    private static final int THREAD_POOL_SIZE = 10; // for async photo/QR

    private static final DateTimeFormatter FORMAT1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy"); // 12-May-2003
    private static final DateTimeFormatter FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");   // 2003-05-12
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{FORMAT1, FORMAT2};

    /**
     * Bulk upload optimized for large files.
     */
    @Transactional
    public void uploadStudentsBulk(MultipartFile file, String imagesFolderPath) throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Student> batchList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            var sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                StudentUploadDTO dto = parseRowToDTO(row);
                if (dto.getRollNumber() == null || dto.getRollNumber().isBlank()) continue;

                try {
                    Student student = processStudentRow(dto, imagesFolderPath, executor);
                    batchList.add(student);

                    if (batchList.size() >= BATCH_SIZE) {
                        studentRepository.saveAll(batchList);
                        batchList.clear();
                    }

                } catch (Exception e) {
                    log.error("Failed to process student {} at row {}: {}", dto.getRollNumber(), i + 1, e.getMessage());
                }
            }

            // save remaining students
            if (!batchList.isEmpty()) {
                studentRepository.saveAll(batchList);
            }

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                    log.warn("Photo/QR tasks did not finish in time");
                }
            } catch (InterruptedException e) {
                log.error("Executor interrupted", e);
            }
        }

        log.info("Bulk upload completed successfully");
    }

    private Student processStudentRow(StudentUploadDTO dto, String imagesFolderPath, ExecutorService executor) throws IOException {

        Contact contact = mapToContact(dto);

        var department = departmentRepository.findByDepartmentCode(dto.getDepartmentCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department code: " + dto.getDepartmentCode()));
        var course = courseRepository.findByCourseCode(dto.getCourseCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid course code: " + dto.getCourseCode()));

        Optional<Student> existing = studentRepository.findByRollNumber(dto.getRollNumber());
        Student student;

        if (existing.isPresent()) {
            student = existing.get();
            student.setFirstName(dto.getFirstName());
            student.setMiddleName(dto.getMiddleName());
            student.setLastName(dto.getLastName());
            student.setDob(dto.getDob());
            student.setGender(dto.getGender());
            student.setContact(contact);
            student.setDepartment(department);
            student.setCourse(course);
            student.setAdmissionYear(dto.getAdmissionYear());
            student.setAdmissionNumber(dto.getAdmissionNumber());
            student.setBloodGroup(dto.getBloodGroup());
            student.setCategory(dto.getCategory());
            student.setNationality(dto.getNationality());
        } else {
            student = Student.builder()
                    .firstName(dto.getFirstName())
                    .middleName(dto.getMiddleName())
                    .lastName(dto.getLastName())
                    .dob(dto.getDob())
                    .gender(dto.getGender())
                    .contact(contact)
                    .department(department)
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
        
        

        // async photo and QR
        executor.submit(() -> handlePhotoAndQr(dto, imagesFolderPath, student));

        return student;
    }

    private void handlePhotoAndQr(StudentUploadDTO dto, String imagesFolderPath, Student student) {
        try {
            // Handle photo
            File imgFolder = new File(imagesFolderPath);
            if (imgFolder.exists() && imgFolder.isDirectory()) {
                File[] matchingFiles = imgFolder.listFiles(f -> {
                    String name = f.getName().toLowerCase();
                    return name.startsWith(dto.getRollNumber().toLowerCase()) &&
                            (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));
                });

                if (matchingFiles != null && matchingFiles.length > 0) {
                    File photoFile = matchingFiles[0];
                    byte[] bytes = Files.readAllBytes(photoFile.toPath());
                    String url = photoStorageService.updateStudentPhoto(dto.getRollNumber(), bytes);
                    student.setPhotoUrl(url);
                }
            }

            createUserForStudent(student);

        } catch (Exception e) {
            log.error("Failed to process photo/QR for student {}: {}", dto.getRollNumber(), e.getMessage());
        }
    }
    
    private void createUserForStudent(Student student) {
        Contact contact = student.getContact();

        var user = commonUserService.createUser(
                student.getRollNumber(),   // username
                RoleEnum.STUDENT,              // role
                student.getStudentId(),    // referenceId
                "stu@123",          // default password
                contact                    // contact info
        );

        if (user == null) {
            throw new RuntimeException("Failed to create user for student: " + student.getRollNumber());
        }

        student.setUser(user);
        studentRepository.save(student);

        log.info("User created for student: {}", student.getRollNumber());
    }

    private StudentUploadDTO parseRowToDTO(Row r) {
        StudentUploadDTO dto = new StudentUploadDTO();
        dto.setFirstName(getCell(r, 0));
        dto.setMiddleName(getCell(r, 1));
        dto.setLastName(getCell(r, 2));

        dto.setDob(parseDate(r.getCell(3))); // pass cell directly

        dto.setGender(getCell(r, 4));
        dto.setPhone(getCell(r, 5));
        dto.setEmail(getCell(r, 6));
        dto.setAltPhone(getCell(r, 7));
        dto.setAddressLine1(getCell(r, 8));
        dto.setAddressLine2(getCell(r, 9));
        dto.setCity(getCell(r, 10));
        dto.setDistrict(getCell(r, 11));
        dto.setState(getCell(r, 12));
        dto.setCountry(getCell(r, 13));
        dto.setPin(getCell(r, 14));
        dto.setDepartmentCode(getCell(r, 15));
        dto.setCourseCode(getCell(r, 16));
        dto.setAdmissionYear(parseInteger(getCell(r, 17)));
        dto.setAdmissionNumber(getCell(r, 18));
        dto.setRollNumber(getCell(r, 19));
        dto.setBloodGroup(getCell(r, 20));
        dto.setCategory(getCell(r, 21));
        dto.setNationality(getCell(r, 22));
        dto.setAdhaarNumber(getCell(r, 23));

        return dto;
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String dateStr = cell.getStringCellValue().trim();
                for (DateTimeFormatter fmt : DATE_FORMATS) {
                    try {
                        return LocalDate.parse(dateStr, fmt);
                    } catch (DateTimeParseException ignored) {}
                }
                throw new IllegalArgumentException("Invalid date format: " + dateStr);

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                } else {
                    // numeric Excel date serial
                    return LocalDate.of(1899, 12, 30).plusDays((long) cell.getNumericCellValue());
                }

            default:
                return null;
        }
    }

    private int parseInteger(String value) {
        if (value == null || value.isBlank()) return 0;
        // handle numeric format like 2023.0
        if (value.contains(".")) {
            value = value.substring(0, value.indexOf('.'));
        }
        return Integer.parseInt(value);
    }

    private String getCell(Row r, int col) {
        Cell c = r.getCell(col);
        return c == null ? "" : c.toString().trim();
    }

    private Contact mapToContact(StudentUploadDTO dto) {
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
}
