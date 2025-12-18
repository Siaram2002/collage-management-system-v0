package com.cms.college.controller;


import com.cms.college.dto.DepartmentRequestDTO;
import com.cms.college.dto.DepartmentResponseDTO;
//import com.cms.college.service.DepartmentService;
import com.cms.college.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(
            @RequestBody DepartmentRequestDTO request
    ) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }
}

