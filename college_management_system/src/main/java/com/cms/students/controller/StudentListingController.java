package com.cms.students.controller;

import com.cms.common.ApiResponse;
import com.cms.students.dto.StudentFilterRequest;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.mappers.StudentMapper;

import com.cms.students.services.StudentListingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentListingController {

    private final StudentListingService studentListingService;
    private final StudentMapper studentMapper;

    // ----------------------------------------------------------------
    // 1. SEARCH STUDENTS (POST)
    // ----------------------------------------------------------------
    @PostMapping("/search")
    public ResponseEntity<ApiResponse> searchStudents(@RequestBody StudentFilterRequest filter) {
        log.info("Searching students with filters: {}", filter);

        try {
            Page<StudentProfileDTO> result = studentListingService.getStudents(filter);

            return ResponseEntity.ok(
                    ApiResponse.success("Students fetched successfully", result)
            );

        } catch (Exception e) {
            log.error("Error while searching students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Internal server error"));
        }
    }
}
