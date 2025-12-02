package com.cms.students.dto;



import com.cms.students.enums.StudentStatus;
import lombok.Data;

@Data
public class StudentFilterRequest {
    private Long departmentId;
    private Long courseId;
    private StudentStatus status;
    private String keyword; // search by name, roll, admission
    private int page = 0;
    private int size = 20;
    private String sortBy = "rollNumber";
    private String sortDir = "asc"; // or "desc"
}
