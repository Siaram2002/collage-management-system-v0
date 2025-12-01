package com.cms.students.controller;

import com.cms.common.ApiResponse;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.models.Student;
import com.cms.students.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/students", produces = "application/json")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StudentController {

    private final StudentService studentService;

    // -----------------------------
    // CREATE STUDENT
    // -----------------------------
    @PostMapping
    public ResponseEntity<ApiResponse> createStudent(@Valid @RequestBody Student student) {
        log.info("Creating student: {}", student.getRollNumber());
        Student saved = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", saved));
    }

    // -----------------------------
    // GET STUDENT BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStudent(@PathVariable Long id) {
        log.info("Fetching student by ID={}", id);
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student found", student));
    }

    // -----------------------------
    // GET STUDENT BY ROLL NUMBER
    // -----------------------------
    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<ApiResponse> getStudentByRoll(@PathVariable String rollNumber) {
        log.info("Fetching student by roll number={}", rollNumber);
        Student student = studentService.getByRollNumber(rollNumber);
        return ResponseEntity.ok(ApiResponse.success("Student found", student));
    }

    // -----------------------------
    // UPDATE STUDENT
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStudent(@PathVariable Long id,
                                                      @Valid @RequestBody Student student) {
        log.info("Updating student ID={}", id);
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));
    }

    // -----------------------------
    // DELETE STUDENT
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student ID={}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"));
    }

    // -----------------------------
    // UPDATE STUDENT STATUS
    // -----------------------------
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStudentStatus(@PathVariable Long id,
                                                           @RequestParam StudentStatus status) {
        log.info("Updating status for student ID={} to {}", id, status);
        Student updated = studentService.updateStudentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Student status updated", updated));
    }

    // -----------------------------
    // UPDATE ENROLLMENT STATUS
    // -----------------------------
    @PatchMapping("/{id}/enrollment-status")
    public ResponseEntity<ApiResponse> updateEnrollmentStatus(@PathVariable Long id,
                                                              @RequestParam EnrollmentStatus status) {
        log.info("Updating enrollment status for student ID={} to {}", id, status);
        Student updated = studentService.updateEnrollmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Enrollment status updated", updated));
    }

    // -----------------------------
    // UPLOAD SINGLE PHOTO
    // -----------------------------
    @PostMapping("/{id}/photo")
    public ResponseEntity<ApiResponse> uploadPhoto(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) {
        log.info("Uploading photo for student ID={}", id);
        String url = studentService.uploadStudentPhoto(id, file);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", url));
    }

    // -----------------------------
    // BULK PHOTO UPLOAD (ASYNC)
    // -----------------------------
    @PostMapping("/photos/bulk")
    public ResponseEntity<ApiResponse> uploadPhotosBulk(@RequestBody Map<Long, byte[]> studentPhotoMap) {
        log.info("Initiating bulk photo upload for {} students", studentPhotoMap.size());
        studentService.uploadPhotosBulkAsync(studentPhotoMap);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Bulk photo upload started"));
    }

    // -----------------------------
    // ACTIVATE STUDENT USER
    // -----------------------------
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activateStudentUser(@PathVariable Long id) {
        log.info("Activating user for student ID={}", id);
        Student student = studentService.activateStudentUser(id);
        return ResponseEntity.ok(ApiResponse.success("Student user activated", student));
    }

    // -----------------------------
    // BULK STUDENT UPLOAD (CSV + IMAGES)
    // -----------------------------
    @PostMapping("/bulk-upload")
    public ResponseEntity<ApiResponse> uploadStudentsBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("imageFolderPath") String imageFolderPath
    ) {
        try {
            log.info("Bulk student upload initiated: file={} folder={}", file.getOriginalFilename(), imageFolderPath);
            studentService.uploadStudentsBulk(file, imageFolderPath);
            return ResponseEntity.ok(ApiResponse.success("Bulk student upload initiated"));
        } catch (IOException e) {
            log.error("Failed bulk student upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to upload students: " + e.getMessage()));
        }
    }

}
