package com.cms.college.services;


import com.cms.college.dto.DepartmentRequestDTO;
import com.cms.college.dto.DepartmentResponseDTO;
import com.cms.college.models.Department;
import com.cms.college.reporitories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO request) {

        if (departmentRepository.existsByDepartmentCode(request.getDepartmentCode())) {
            throw new RuntimeException("Department code already exists");
        }

        Department department = Department.builder()
                .name(request.getName())
                .departmentCode(request.getDepartmentCode())
                .totalSeats(request.getTotalSeats())
                .establishedYear(request.getEstablishedYear())
                .status(request.getStatus())
                .build();

        Department saved = departmentRepository.save(department);

        return DepartmentResponseDTO.builder()
                .departmentId(saved.getDepartmentId())
                .name(saved.getName())
                .departmentCode(saved.getDepartmentCode())
                .totalSeats(saved.getTotalSeats())
                .establishedYear(saved.getEstablishedYear())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }


    public List<DepartmentResponseDTO> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(department -> DepartmentResponseDTO.builder()
                        .departmentId(department.getDepartmentId())
                        .name(department.getName())
                        .departmentCode(department.getDepartmentCode())
                        .totalSeats(department.getTotalSeats())
                        .establishedYear(department.getEstablishedYear())
                        .status(department.getStatus())
                        .createdAt(department.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return DepartmentResponseDTO.builder()
                .departmentId(department.getDepartmentId())
                .name(department.getName())
                .departmentCode(department.getDepartmentCode())
                .totalSeats(department.getTotalSeats())
                .establishedYear(department.getEstablishedYear())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .build();
    }

}

