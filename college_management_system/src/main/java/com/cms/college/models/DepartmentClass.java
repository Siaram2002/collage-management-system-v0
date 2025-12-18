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
        name = "department_class",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"department_id", "class_code"})
        }
)
public class DepartmentClass {

//    department_id = 1
//    class_code = A
//    class_name = CSE-A

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentClassId;

    // Many classes belong to one department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // A, B, C
    @Column(name = "class_code", nullable = false, length = 5)
    private String classCode;

    // CSE-A, CSE-B
    @Column(name = "class_name", nullable = false, length = 20)
    private String className;

    // Semester / Year
    @Column(nullable = false)
    private Integer semester;

//    @Column(nullable = false)
//    private Integer totalStudents;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / INACTIVE

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
