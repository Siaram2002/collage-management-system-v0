package com.cms.attendance.attendance_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportDTO {
    private Long attendanceId;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String admissionNumber;
    private Long lectureId;
    private String subjectName;
    private LocalDate lectureDate;
    private String facultyName;
    private String departmentName;
    private String departmentCode;
    private String className; // e.g., CSE-A, CSE-B
    private String classCode; // A, B, etc.
    private LocalDateTime scanTime;
    private String status;
}

