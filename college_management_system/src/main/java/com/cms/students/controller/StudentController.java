package com.cms.students.controller;

import com.cms.common.ApiResponse;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.exceptions.DuplicateStudentException;
import com.cms.students.exceptions.StudentNotFoundException;
import com.cms.students.mappers.StudentMapper;
import com.cms.students.models.Student;
import com.cms.students.services.StudentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/students", produces = "application/json")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    // ---------------------------------------------------------
    // CREATE STUDENT
    // ---------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse> createStudent(@Valid @RequestBody Student student) {
        log.info("Creating student: {}", student.getRollNumber());
        try {
            Student saved = studentService.createStudent(student);
            StudentProfileDTO dto = studentMapper.toProfileDTO(saved);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Student created successfully", dto));

        } catch (DuplicateStudentException e) {
            log.warn("Duplicate student error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error creating student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to create student"));
        }
    }

    // ---------------------------------------------------------
    // GET STUDENT BY ID
    // ---------------------------------------------------------
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse> getStudentById(@PathVariable Long id) {
        log.info("Fetching student by ID={}", id);
        try {
            Student student = studentService.getStudentById(id);
            StudentProfileDTO dto = studentMapper.toProfileDTO(student);

            return ResponseEntity.ok(ApiResponse.success("Student found", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error fetching student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Internal server error"));
        }
    }

    // ---------------------------------------------------------
    // GET STUDENT BY ROLL NUMBER
    // ---------------------------------------------------------
    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<ApiResponse> getStudentByRollNumber(@PathVariable String rollNumber) {
        log.info("Fetching student by roll number={}", rollNumber);
        try {
            Student student = studentService.getByRollNumber(rollNumber);
            StudentProfileDTO dto = studentMapper.toProfileDTO(student);

            return ResponseEntity.ok(ApiResponse.success("Student found", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Student not found roll={}", rollNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error fetching student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Internal server error"));
        }
    }

    // ---------------------------------------------------------
    // UPDATE STUDENT
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student student
    ) {
        log.info("Updating student ID={}", id);
        try {
            Student updated = studentService.updateStudent(id, student);
            StudentProfileDTO dto = studentMapper.toProfileDTO(updated);

            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Update failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error updating student ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to update student"));
        }
    }

    // ---------------------------------------------------------
    // DELETE STUDENT
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student ID={}", id);
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"));

        } catch (StudentNotFoundException e) {
            log.warn("Delete failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error deleting student ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to delete student"));
        }
    }

    // ---------------------------------------------------------
    // UPDATE STUDENT STATUS
    // ---------------------------------------------------------
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStudentStatus(
            @PathVariable Long id,
            @RequestParam StudentStatus status
    ) {
        log.info("Updating student status ID={} to {}", id, status);
        try {
            Student updated = studentService.updateStudentStatus(id, status);
            StudentProfileDTO dto = studentMapper.toProfileDTO(updated);

            return ResponseEntity.ok(ApiResponse.success("Student status updated", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Update failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error updating status ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to update student status"));
        }
    }

    // ---------------------------------------------------------
    // UPDATE ENROLLMENT STATUS
    // ---------------------------------------------------------
    @PatchMapping("/{id}/enrollment-status")
    public ResponseEntity<ApiResponse> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam EnrollmentStatus status
    ) {
        log.info("Updating enrollment status ID={} to {}", id, status);
        try {
            Student updated = studentService.updateEnrollmentStatus(id, status);
            StudentProfileDTO dto = studentMapper.toProfileDTO(updated);

            return ResponseEntity.ok(ApiResponse.success("Enrollment status updated", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Update failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error updating enrollment status ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to update enrollment status"));
        }
    }

    // ---------------------------------------------------------
    // UPLOAD STUDENT PHOTO
    // ---------------------------------------------------------
    @PostMapping("/{id}/photo")
    public ResponseEntity<ApiResponse> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("Uploading student photo ID={}", id);
        try {
            String url = studentService.uploadStudentPhoto(id, file);
            return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", url));

        } catch (StudentNotFoundException e) {
            log.warn("Upload failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error uploading photo ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to upload photo"));
        }
    }

    // ---------------------------------------------------------
    // ACTIVATE STUDENT USER
    // ---------------------------------------------------------
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activateStudentUser(@PathVariable Long id) {
        log.info("Activating student user ID={}", id);
        try {
            Student student = studentService.activateStudentUser(id);
            StudentProfileDTO dto = studentMapper.toProfileDTO(student);

            return ResponseEntity.ok(ApiResponse.success("Student user activated", dto));

        } catch (StudentNotFoundException e) {
            log.warn("Activate failed. Student not found ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));

        } catch (Exception e) {
            log.error("Error activating user ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to activate student user"));
        }
    }

    // ---------------------------------------------------------
    // BULK UPLOAD STUDENTS
    // ---------------------------------------------------------
    @PostMapping("/bulk-upload")
    public ResponseEntity<ApiResponse> uploadStudentsBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("imageFolderPath") String imageFolderPath
    ) {
        log.info("Bulk upload started: {}", file.getOriginalFilename());
        try {
            studentService.uploadStudentsBulk(file, imageFolderPath);
            return ResponseEntity.ok(ApiResponse.success("Bulk student upload started"));

        } catch (IOException e) {
            log.error("Bulk upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to upload students: " + e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during bulk upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Internal server error"));
        }
    }
}
