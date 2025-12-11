package com.cms.students.controller;

import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.common.ApiResponse;
import com.cms.students.dto.StudentDTO;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.mappers.StudentMapper;
import com.cms.students.mappers.StudentToProfileMapper;
import com.cms.students.models.Student;
import com.cms.students.services.StudentBulkUploadService;
import com.cms.students.services.StudentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;
    private final StudentToProfileMapper profileMapper;

    /**
     * Register a new student with optional photo upload
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerStudent(
            @RequestPart("student") StudentDTO studentDTO,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        log.info("Registering new student: {}", studentDTO.getFirstName());

        try {
            // Prepare Department and Course from codes
            Department dept = new Department();
            dept.setDepartmentCode(studentDTO.getDepartmentCode());

            Course course = new Course();
            course.setCourseCode(studentDTO.getCourseCode());

            // Map DTO to entity
            Student student = StudentMapper.toStudent(studentDTO, dept, course);

            Student savedStudent = studentService.registerStudent(student, photo);
            StudentProfileDTO response = profileMapper.toProfileDTO(savedStudent);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Student registered successfully", response));

        } catch (Exception e) {
            log.error("Error registering student", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Failed to register student", e.getMessage()));
        }
    }

    /**
     * Get all student profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse> getAllStudentProfiles() {
        log.info("Fetching all student profiles");

        try {
            List<StudentProfileDTO> profiles = studentService.getAllStudentProfiles();
            return ResponseEntity.ok(ApiResponse.success("Profiles fetched successfully", profiles));
        } catch (Exception e) {
            log.error("Error fetching student profiles", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to fetch student profiles"));
        }
    }

    /**
     * Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStudentById(@PathVariable Long id) {
        log.info("Fetching student by ID: {}", id);

        try {
            Student student = studentService.getStudentById(id);
            StudentProfileDTO response = profileMapper.toProfileDTO(student);

            return ResponseEntity.ok(ApiResponse.success("Student fetched successfully", response));

        } catch (Exception e) {
            log.error("Error fetching student with ID {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Student not found"));
        }
    }
    
    @GetMapping("/getByRollNumber/{rollNumber}")
    public ResponseEntity<ApiResponse> getStudentByRollNumber(@PathVariable String rollNumber) {
        log.info("Fetching student by ID: {}", rollNumber);

        try {
            Student student = studentService.getStudentByRollNumber(rollNumber);
            StudentProfileDTO response = profileMapper.toProfileDTO(student);

            return ResponseEntity.ok(ApiResponse.success("Student fetched successfully", response));

        } catch (Exception e) {
            log.error("Error fetching student with rollNumber {}", rollNumber, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Student not found"));
        }
    }
    
    private final StudentBulkUploadService studentBulkUploadService;

    /**
     * Upload students Excel + Images folder path
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadBulkStudents(
            @RequestParam("file") MultipartFile excelFile,
            @RequestParam(value = "imagesFolderPath", required = false) String imagesFolderPath
    ) {
        log.info("Received bulk student upload request");

        try {
            if (excelFile == null || excelFile.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.fail("Excel file is required")
                );
            }

            // If user didn't provide images folder path -> use null or default
            if (imagesFolderPath == null || imagesFolderPath.isBlank()) {
                imagesFolderPath = "";
            }

            studentBulkUploadService.uploadStudentsBulk(excelFile, imagesFolderPath);

            return ResponseEntity.ok(
                    ApiResponse.success("Bulk upload completed successfully")
            );

        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(
                    ApiResponse.fail("Bulk upload failed: " + e.getMessage())
            );
        }
    }
}
