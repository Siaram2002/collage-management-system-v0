package com.cms.attendance.attendance_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceSummaryDTO {
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String admissionNumber;
    private Integer totalLectures;
    private Integer presentCount;
    private Integer absentCount;
    private Double attendancePercentage;
    private String status; // GOOD, WARNING, CRITICAL
}

