package com.cms.attendance.lecture.module;

import com.cms.attendance.comman.enums.LectureStatus;
import com.cms.attendance.faculty.module.Faculty;
import com.cms.college.models.DepartmentClass;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "lecture",
        indexes = {
                @Index(name = "idx_lecture_date", columnList = "lecture_date"),
                @Index(name = "idx_department_class", columnList = "department_class_id")
        }
)
public class Lecture {

//    Index	Used for
//    lecture_date	Daily attendance reports
//    department_class_id	Class-wise lecture listing

//    SELECT * FROM lecture
//    WHERE department_class_id = ?
//    AND lecture_date = ?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    /**
     * Which class is attending (CSE-A, CSE-B, etc.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_class_id", nullable = false)
    private DepartmentClass departmentClass;

    /**
     * Which faculty is conducting the lecture
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    /**
     * Subject name (UI + reports only, NOT used in QR)
     */
    @Column(nullable = false, length = 100)
    private String subjectName;

    /**
     * Lecture date
     */
    @Column(nullable = false)
    private LocalDate lectureDate;

    /**
     * Lecture start time
     */
    @Column(nullable = false)
    private LocalTime startTime;

    /**
     * Lecture end time
     */
    @Column(nullable = false)
    private LocalTime endTime;

    /**
     * Type of lecture
     * REGULAR / LAB / EXTRA / EXAM
     */
    @Column(nullable = false, length = 20)
    private String lectureType;

    /**
     * Lecture location (optional but realistic)
     */
    @Column(length = 50)
    private String room;

    /**
     * Academic semester (e.g., 3, 5, 7)
     */
    @Column(nullable = false)
    private Integer semester;

    /**
     * Academic year (e.g., 2024-25)
     */
    @Column(nullable = false, length = 20)
    private String academicYear;

    /**
     * SCHEDULED / ACTIVE / EXPIRED / CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LectureStatus status;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

