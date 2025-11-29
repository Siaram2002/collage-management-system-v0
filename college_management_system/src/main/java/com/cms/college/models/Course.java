package com.cms.college.models;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;



import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "course",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_code"}),
        @UniqueConstraint(columnNames = {"name", "department_id"})
    }
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department; // Each course belongs to a department

    @Column(nullable = false, length = 100)
    private String name; // e.g., "B.Tech Computer Science"

    @Column(nullable = false, length = 20)
    private String courseCode; // Unique code, e.g., "CSE-BT"

    @Column(nullable = false, length = 20)
    private String courseType; // UG / PG / Diploma / Certificate

    @Column(nullable = false)
    private Double durationYears; // Example: 4.0 for 4-year UG

    @Column(nullable = false)
    private Integer totalSemesters; // Example: 8 for 4-year UG

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / INACTIVE

    @Column(columnDefinition = "TEXT")
    private String eligibilityCriteria; // Optional real-time field

    @Column(length = 255)
    private String syllabusUrl; // Optional, link to syllabus

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

