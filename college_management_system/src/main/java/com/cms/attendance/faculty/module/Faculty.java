package com.cms.attendance.faculty.module;

import com.cms.college.models.Contact;
import com.cms.college.models.Department;
import com.cms.college.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "faculty",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"faculty_code"})
        }
)
public class Faculty {

    // ---------------- Primary Key ----------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facultyId;

    // ---------------- Faculty Code (Generated) ----------------
    @Column(name = "faculty_code", nullable = false, length = 50, unique = true)
    private String facultyCode;

    // ---------------- Name ----------------
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    // ---------------- Personal Info ----------------
    private LocalDate dob;

    // ---------------- Job Info ----------------
    @Column(length = 50, nullable = false)
    private String designation;

    @Column(length = 20, nullable = false)
    private String status;   // ACTIVE / INACTIVE

    // ---------------- Department ----------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // ---------------- Login User ----------------
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    // ---------------- Contact ----------------
    // ---------------- Contact ----------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
    private Contact contact;



    // ---------------- Audit ----------------
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ---------------- Media ----------------
    @Column(length = 255)
    private String photoUrl;   // faculty profile photo

}
