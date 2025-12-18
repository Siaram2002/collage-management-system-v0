package com.cms.attendance.lecture.dto;

import com.cms.attendance.comman.enums.LectureStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LectureCreateRequestDTO {

    private Long departmentClassId;
    private Long facultyId;

    private String subjectName;

    private LocalDate lectureDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String lectureType;   // REGULAR / LAB / EXTRA / EXAM
    private String room;

    private Integer semester;
    private String academicYear;

    private LectureStatus status; // SCHEDULED / ACTIVE / EXPIRED / CANCELLED
}
