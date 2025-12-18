package com.cms.college.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DepartmentResponseDTO {

    private Long departmentId;
    private String name;
    private String departmentCode;
    private Integer totalSeats;
    private Integer establishedYear;
    private String status;
    private LocalDateTime createdAt;
}