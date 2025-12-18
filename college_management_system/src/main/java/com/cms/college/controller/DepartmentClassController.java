package com.cms.college.controller;


import com.cms.college.dto.DepartmentClassRequestDTO;
import com.cms.college.dto.DepartmentClassResponseDTO;
import com.cms.college.services.DepartmentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department-classes")
@RequiredArgsConstructor
public class DepartmentClassController {

    private final DepartmentClassService departmentClassService;

    @PostMapping
    public ResponseEntity<DepartmentClassResponseDTO> createDepartmentClass(
            @RequestBody DepartmentClassRequestDTO request
    ) {
        return ResponseEntity.ok(departmentClassService.createDepartmentClass(request));
    }
    @GetMapping
    public ResponseEntity<List<DepartmentClassResponseDTO>> getAllDepartmentClasses() {
        return ResponseEntity.ok(departmentClassService.getAllDepartmentClasses());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DepartmentClassResponseDTO>> getClassesByDepartment(
            @PathVariable Long departmentId
    ) {
        return ResponseEntity.ok(departmentClassService.getClassesByDepartment(departmentId));
    }


}
