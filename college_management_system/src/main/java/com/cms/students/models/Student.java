package com.cms.students.models;

import jakarta.persistence.*;

import lombok.*;

import org.apache.el.parser.AstFalse;
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
@Table(name = "student", uniqueConstraints = { @UniqueConstraint(columnNames = { "rollNumber" }),
		@UniqueConstraint(columnNames = { "admissionNumber" }) })
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long studentId;

	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(length = 50)
	private String middleName;

	@Column(length = 50)
	private String lastName;

	@Column(nullable = false)
	private LocalDate dob;

	@Column(nullable = false, length = 10)
	private String gender;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_id", nullable = false)
	private Contact contact;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guardian_contact_id")
	private Contact guardianContact;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(nullable = false)
	private Integer admissionYear;

	@Column(nullable = false, length = 20)
	private String admissionNumber;

	@Column(nullable = false, length = 20)
	private String rollNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StudentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EnrollmentStatus enrollmentStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 10)
	private String bloodGroup;

	@Column(length = 20)
	private String category;

	@Column(length = 50)
	private String nationality;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "transport_route_id")
//    private TransportRoute transportRoute;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "hostel_id")
//    private Hostel hostel;

	@Column(length = 255)
	private String photoUrl;

	@Column(length = 255)
	private String qrUrl; // QR code URL for ID card / attendance / library etc.

//	@Column(nullable = false)
//	private Boolean profileVerified = false;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
