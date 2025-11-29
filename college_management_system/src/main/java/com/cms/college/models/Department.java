package com.cms.college.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "department",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"short_code"})
    }
)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(nullable = false, length = 150)
    private String name; // Full department name, e.g., "Computer Science"

    @Column(nullable = false, length = 20)
    private String shortCode; // Short code, e.g., "CSE"

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "hod_id")
//    private Staff hod; // Optional Head of Department

    @Column(nullable = false)
    private Integer totalSeats; // Number of students for admissions

    @Column(nullable = false)
    private Integer establishedYear; // Year department was founded

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / INACTIVE

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses; // Courses under this department

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
