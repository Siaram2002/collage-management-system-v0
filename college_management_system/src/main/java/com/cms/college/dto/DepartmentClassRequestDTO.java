package com.cms.college.dto;
import lombok.Data;

@Data
public class DepartmentClassRequestDTO {
    private Long departmentId;
    private String classCode;   // A, B, C
    private String className;   // CSE-A
    private Integer semester;
    private String status;
}
