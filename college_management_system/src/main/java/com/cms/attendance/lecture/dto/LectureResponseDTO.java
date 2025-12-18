package com.cms.attendance.lecture.dto;

import com.cms.attendance.comman.enums.LectureStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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

//    private LocalDate lectureDate;
//    private LocalTime startTime;
//    private LocalTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lectureDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String lectureType;
    private String room;

    private Integer semester;
    private String academicYear;

    private LectureStatus status;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
