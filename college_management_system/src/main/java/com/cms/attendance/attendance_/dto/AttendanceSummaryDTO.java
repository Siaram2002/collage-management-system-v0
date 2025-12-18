package com.cms.attendance.attendance_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String className;
    private String classCode;
    private Integer totalLectures;
    private Integer totalStudents;
    private Integer presentCount;
    private Integer absentCount;
    private Double attendancePercentage;
    private List<StudentAttendanceSummaryDTO> studentSummaries;
}

