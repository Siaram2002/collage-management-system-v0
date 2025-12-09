package com.cms.students.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.college.models.Contact;
import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.college.models.User;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"rollNumber"}),
        @UniqueConstraint(columnNames = {"admissionNumber"})
})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(length = 50)
    private String middleName;

    @Size(max = 50)
    @Column(length = 50)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Column(nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    @Column(nullable = false, length = 10)
    private String gender;
    
    @Size(max = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be a 12-digit numeric value")
    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @NotNull(message = "Contact is required")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @NotNull(message = "Department is required")
    @ManyToOne(fetch = FetchType.EAGER) // Fetch EAGER for small lookup tables
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Admission year is required")
    @Min(value = 1900)
    @Column(nullable = false)
    private Integer admissionYear;

    @NotBlank(message = "Admission number is required")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String admissionNumber;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String rollNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ENROLLED;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;
    
    

    @Size(max = 10)
    @Column(length = 10)
    private String bloodGroup;

    @Size(max = 20)
    @Column(length = 20)
    private String category;

    @Size(max = 50)
    @Column(length = 50)
    private String nationality;

    @Size(max = 255)
    @Column(length = 255)
    private String photoUrl;



    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
