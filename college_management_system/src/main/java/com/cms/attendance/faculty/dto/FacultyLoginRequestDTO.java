package com.cms.attendance.faculty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacultyLoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
