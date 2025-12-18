package com.cms.college.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentClassResponseDTO {

    private Long departmentClassId;
    private Long departmentId;
    private String classCode;
    private String className;
    private Integer semester;
    private String status;
}
