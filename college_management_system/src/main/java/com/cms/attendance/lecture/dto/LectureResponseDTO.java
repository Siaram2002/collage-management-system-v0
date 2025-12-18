package com.cms.attendance.lecture.dto;

import com.cms.attendance.comman.enums.LectureStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
public class LectureResponseDTO {

    private Long lectureId;

    private Long departmentClassId;
    private Long facultyId;

    private String subjectName;

    private LocalDate lectureDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String lectureType;
    private String room;

    private Integer semester;
    private String academicYear;

    private LectureStatus status;

    private LocalDateTime createdAt;
}
