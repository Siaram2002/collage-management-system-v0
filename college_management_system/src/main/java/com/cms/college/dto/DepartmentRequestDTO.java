package com.cms.college.dto;

import lombok.Data;

@Data
public class DepartmentRequestDTO {

    private String name;
    private String departmentCode;
    private Integer totalSeats;
    private Integer establishedYear;
    private String status; // ACTIVE / INACTIVE
}
