package com.cms.students.controller;

import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.college.reporitories.CourseRepository;
import com.cms.college.reporitories.DepartmentRepository;
import com.cms.common.ApiResponse;
import com.cms.students.dto.StudentDTO;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.mappers.StudentMapper;
import com.cms.students.mappers.StudentToProfileMapper;
import com.cms.students.models.Student;
import com.cms.students.services.StudentBulkUploadService;
import com.cms.students.services.StudentService;
import com.cms.students.services.StudentQRCodeService;
import com.cms.transport.route.service.RouteService;
import com.cms.transport.route.dto.RouteDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final StudentBulkUploadService studentBulkUploadService;
    private final StudentQRCodeService studentQRCodeService;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final RouteService routeService;

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

    /**
     * Get student by Roll Number
     */
    @GetMapping("/getByRollNumber/{rollNumber}")
    public ResponseEntity<ApiResponse> getStudentByRollNumber(@PathVariable String rollNumber) {
        log.info("Fetching student by rollNumber: {}", rollNumber);

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

    /**
     * Get all student profiles (alias for /profiles)
     */
    @GetMapping("/profile_res")
    public ResponseEntity<ApiResponse> getAllStudentProfilesRes() {
        return getAllStudentProfiles();
    }

    /**
     * Get all departments
     */
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse> getAllDepartments() {
        log.info("Fetching all departments");
        try {
            List<Department> departments = departmentRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", departments));
        } catch (Exception e) {
            log.error("Error fetching departments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to fetch departments"));
        }
    }

    /**
     * Get all courses with department info
     */
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse> getAllCourses() {
        log.info("Fetching all courses");
        try {
            List<Course> courses = courseRepository.findAll();
            // Map to include department info (since @JsonBackReference hides it)
            List<Map<String, Object>> courseList = courses.stream()
                    .map(course -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("courseId", course.getCourseId());
                        map.put("courseCode", course.getCourseCode());
                        map.put("name", course.getName());
                        map.put("courseType", course.getCourseType());
                        map.put("durationYears", course.getDurationYears());
                        map.put("totalSemesters", course.getTotalSemesters());
                        // Include department info
                        if (course.getDepartment() != null) {
                            Map<String, Object> deptMap = new HashMap<>();
                            deptMap.put("departmentId", course.getDepartment().getDepartmentId());
                            deptMap.put("departmentCode", course.getDepartment().getDepartmentCode());
                            deptMap.put("name", course.getDepartment().getName());
                            map.put("department", deptMap);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Courses fetched successfully", courseList));
        } catch (Exception e) {
            log.error("Error fetching courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to fetch courses"));
        }
    }

    /**
     * Get all routes with steps
     */
    @GetMapping("/routes")
    public ResponseEntity<ApiResponse> getAllRoutes() {
        log.info("Fetching all routes with steps");
        try {
            List<RouteDTO> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(ApiResponse.success("Routes fetched successfully", routes));
        } catch (Exception e) {
            log.error("Error fetching routes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to fetch routes"));
        }
    }

    /**
     * Generate roll number
     */
    @GetMapping("/generate-roll-number")
    public ResponseEntity<ApiResponse> generateRollNumber(
            @RequestParam String departmentCode,
            @RequestParam Integer admissionYear) {
        log.info("Generating roll number for department: {}, year: {}", departmentCode, admissionYear);
        try {
            String rollNumber = studentService.generateRollNumber(departmentCode, admissionYear);
            return ResponseEntity.ok(ApiResponse.success("Roll number generated", rollNumber));
        } catch (Exception e) {
            log.error("Error generating roll number", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Failed to generate roll number: " + e.getMessage()));
        }
    }

    /**
     * Generate admission number
     */
    @GetMapping("/generate-admission-number")
    public ResponseEntity<ApiResponse> generateAdmissionNumber(
            @RequestParam Integer admissionYear) {
        log.info("Generating admission number for year: {}", admissionYear);
        try {
            String admissionNumber = studentService.generateAdmissionNumber(admissionYear);
            return ResponseEntity.ok(ApiResponse.success("Admission number generated", admissionNumber));
        } catch (Exception e) {
            log.error("Error generating admission number", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Failed to generate admission number: " + e.getMessage()));
        }
    }

    /**
     * Generate QR codes for all students who don't have one yet
     */
    @PostMapping("/generate-qr-codes")
    public ResponseEntity<ApiResponse> generateQRCodesForAllStudents() {
        log.info("Generating QR codes for all students");
        try {
            int generated = studentQRCodeService.generateQRCodesForAllStudents();
            return ResponseEntity.ok(ApiResponse.success(
                    "QR codes generated successfully", 
                    "Generated QR codes for " + generated + " students"
            ));
        } catch (Exception e) {
            log.error("Error generating QR codes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to generate QR codes: " + e.getMessage()));
        }
    }

    /**
     * Regenerate QR codes for all students (overwrites existing)
     */
    @PostMapping("/regenerate-qr-codes")
    public ResponseEntity<ApiResponse> regenerateQRCodesForAllStudents() {
        log.info("Regenerating QR codes for all students");
        try {
            int generated = studentQRCodeService.regenerateQRCodesForAllStudents();
            return ResponseEntity.ok(ApiResponse.success(
                    "QR codes regenerated successfully", 
                    "Regenerated QR codes for " + generated + " students"
            ));
        } catch (Exception e) {
            log.error("Error regenerating QR codes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to regenerate QR codes: " + e.getMessage()));
        }
    }

    /**
     * Update student by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStudent(
            @PathVariable Long id,
            @RequestPart("student") StudentDTO studentDTO,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        log.info("Updating student with ID: {}", id);

        try {
            // Get existing student
            Student existingStudent = studentService.getStudentById(id);

            // Prepare Department and Course from codes
            Department dept = new Department();
            dept.setDepartmentCode(studentDTO.getDepartmentCode());

            Course course = new Course();
            course.setCourseCode(studentDTO.getCourseCode());

            // Map DTO to entity (preserve existing IDs and relationships)
            Student updatedStudent = StudentMapper.toStudent(studentDTO, dept, course);
            updatedStudent.setStudentId(existingStudent.getStudentId());
            updatedStudent.setUser(existingStudent.getUser());
            updatedStudent.setContact(existingStudent.getContact());
            updatedStudent.setCreatedAt(existingStudent.getCreatedAt());

            // Update student
            Student savedStudent = studentService.updateStudent(updatedStudent, photo);
            StudentProfileDTO response = profileMapper.toProfileDTO(savedStudent);

            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", response));

        } catch (Exception e) {
            log.error("Error updating student with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Failed to update student: " + e.getMessage()));
        }
    }
}
