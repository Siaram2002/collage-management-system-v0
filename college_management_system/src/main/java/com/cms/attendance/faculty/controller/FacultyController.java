package com.cms.attendance.faculty.controller;

import com.cms.attendance.faculty.dto.FacultyLoginRequestDTO;
import com.cms.attendance.faculty.dto.FacultyProfileDTO;
import com.cms.attendance.faculty.dto.FacultyRequestDTO;
import com.cms.attendance.faculty.dto.FacultyResponseDTO;
import com.cms.attendance.faculty.service.FacultyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FacultyProfileDTO> createFaculty(
            @RequestPart("faculty") @Valid String facultyJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws Exception {

        FacultyRequestDTO dto =
                objectMapper.readValue(facultyJson, FacultyRequestDTO.class);

        return ResponseEntity.ok(facultyService.createFaculty(dto, photo));
    }


    @GetMapping
    public ResponseEntity<List<FacultyResponseDTO>> getAllFaculty() {
        return ResponseEntity.ok(facultyService.getAllFaculty());
    }


    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDTO> getFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<FacultyProfileDTO> facultyLogin(
            @RequestBody @Valid FacultyLoginRequestDTO request
    ) {
        return ResponseEntity.ok(
                facultyService.loginFaculty(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    }

}
