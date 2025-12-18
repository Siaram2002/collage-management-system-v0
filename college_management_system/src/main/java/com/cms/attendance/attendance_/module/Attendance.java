package com.cms.attendance.attendance_.module;

import com.cms.attendance.lecture.module.Lecture;
import com.cms.students.models.Student;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"lecture_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_attendance_lecture", columnList = "lecture_id"),
                @Index(name = "idx_attendance_student", columnList = "student_id")
        }
)

public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime scanTime;

    @Column(nullable = false)
    private String status; // PRESENT
}

