package com.cms.attendance.lecture.controller;


import com.cms.attendance.lecture.dto.LectureCreateRequestDTO;
import com.cms.attendance.lecture.dto.LectureResponseDTO;
import com.cms.attendance.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    // CREATE
    @PostMapping
    public ResponseEntity<LectureResponseDTO> createLecture(
            @RequestBody LectureCreateRequestDTO request
    ) {
        return ResponseEntity.ok(lectureService.createLecture(request));
    }

    // GET BY CLASS
    @GetMapping("/class/{departmentClassId}")
    public ResponseEntity<List<LectureResponseDTO>> getByClass(
            @PathVariable Long departmentClassId
    ) {
        return ResponseEntity.ok(
                lectureService.getLecturesByDepartmentClass(departmentClassId)
        );
    }

    // GET BY CLASS + DATE
    @GetMapping("/class/{departmentClassId}/date/{date}")
    public ResponseEntity<List<LectureResponseDTO>> getByClassAndDate(
            @PathVariable Long departmentClassId,
            @PathVariable LocalDate date
    ) {
        return ResponseEntity.ok(
                lectureService.getLecturesByClassAndDate(departmentClassId, date)
        );
    }
}
