package com.cms.college.services;


import com.cms.college.dto.DepartmentClassRequestDTO;
import com.cms.college.dto.DepartmentClassResponseDTO;
import com.cms.college.models.Department;
import com.cms.college.models.DepartmentClass;
import com.cms.college.reporitories.DepartmentClassRepository;
import com.cms.college.reporitories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DepartmentClassService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentClassRepository departmentClassRepository;

    public DepartmentClassResponseDTO createDepartmentClass(DepartmentClassRequestDTO request) {

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        DepartmentClass departmentClass = DepartmentClass.builder()
                .department(department)
                .classCode(request.getClassCode())
                .className(request.getClassName())
                .semester(request.getSemester())
                .status(request.getStatus())
                .build();

        DepartmentClass saved = departmentClassRepository.save(departmentClass);

        return DepartmentClassResponseDTO.builder()
                .departmentClassId(saved.getDepartmentClassId())
                .departmentId(department.getDepartmentId())
                .classCode(saved.getClassCode())
                .className(saved.getClassName())
                .semester(saved.getSemester())
                .status(saved.getStatus())
                .build();
    }

    public List<DepartmentClassResponseDTO> getAllDepartmentClasses() {

        return departmentClassRepository.findAll()
                .stream()
                .map(dc -> DepartmentClassResponseDTO.builder()
                        .departmentClassId(dc.getDepartmentClassId())
                        .departmentId(dc.getDepartment().getDepartmentId())
                        .classCode(dc.getClassCode())
                        .className(dc.getClassName())
                        .semester(dc.getSemester())
                        .status(dc.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DepartmentClassResponseDTO> getClassesByDepartment(Long departmentId) {

        return departmentClassRepository.findByDepartment_DepartmentId(departmentId)
                .stream()
                .map(dc -> DepartmentClassResponseDTO.builder()
                        .departmentClassId(dc.getDepartmentClassId())
                        .departmentId(dc.getDepartment().getDepartmentId())
                        .classCode(dc.getClassCode())
                        .className(dc.getClassName())
                        .semester(dc.getSemester())
                        .status(dc.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}
