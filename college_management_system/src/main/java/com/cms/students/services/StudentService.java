package com.cms.students.services;

import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.mappers.StudentToProfileMapper;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import com.cms.students.services.media.PhotoStorageService;
import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;
import com.cms.college.models.Contact;
import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.college.reporitories.CourseRepository;
import com.cms.college.reporitories.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

	private final StudentRepository studentRepository;
	private final DepartmentRepository departmentRepository;
	private final CourseRepository courseRepository;

	private final CommonUserService commonUserService;
	private final PhotoStorageService photoStorageService;
	private final StudentQRCodeService studentQRCodeService;

	private final StudentToProfileMapper profileMapper;

	private static final RoleEnum STUDENT_ROLE = RoleEnum.STUDENT;
	private static final String DEFAULT_PASSWORD = "stud@123";

	// =====================================================
	// REGISTER STUDENT (COURSE OPTIONAL)
	// =====================================================
	@Transactional
	public Student registerStudent(Student student, MultipartFile photoFile) {

		// -------------------------------
		// VALIDATIONS
		// -------------------------------
		if (studentRepository.existsByRollNumber(student.getRollNumber())) {
			throw new RuntimeException("Roll number already exists: " + student.getRollNumber());
		}

		if (studentRepository.existsByAdmissionNumber(student.getAdmissionNumber())) {
			throw new RuntimeException(
					"Admission number '" + student.getAdmissionNumber() + "' already exists.");
		}

		// -------------------------------
		// RESOLVE DEPARTMENT (MANDATORY)
		// -------------------------------
		Department resolvedDept = departmentRepository
				.findByDepartmentCode(student.getDepartment().getDepartmentCode())
				.orElseThrow(() -> new RuntimeException(
						"Invalid department code: " + student.getDepartment().getDepartmentCode()));

		// -------------------------------
		// RESOLVE COURSE (OPTIONAL)
		// -------------------------------
		Course resolvedCourse = null;

		if (student.getCourse() != null
				&& student.getCourse().getCourseCode() != null
				&& !student.getCourse().getCourseCode().isBlank()) {

			resolvedCourse = courseRepository
					.findByCourseCode(student.getCourse().getCourseCode())
					.orElseThrow(() -> new RuntimeException(
							"Invalid course code: " + student.getCourse().getCourseCode()));
		}

		student.setDepartment(resolvedDept);
		student.setCourse(resolvedCourse); // CAN BE NULL

		Contact contact = student.getContact();

		// -------------------------------
		// SAVE STUDENT
		// -------------------------------
		Student saved = studentRepository.save(student);

		// -------------------------------
		// OPTIONAL PHOTO UPLOAD
		// -------------------------------
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				String photoUrl = photoStorageService
						.storeStudentPhoto(saved.getRollNumber(), photoFile.getBytes());
				saved.setPhotoUrl(photoUrl);
				saved = studentRepository.save(saved);
			} catch (Exception e) {
				throw new RuntimeException("Failed to upload student photo", e);
			}
		}

		// -------------------------------
		// CREATE USER ACCOUNT
		// -------------------------------
		var user = commonUserService.createUser(
				saved.getRollNumber(),
				STUDENT_ROLE,
				saved.getStudentId(),
				DEFAULT_PASSWORD,
				contact
		);

		if (user == null) {
			throw new RuntimeException("Failed to create user for student: " + saved.getRollNumber());
		}

		saved.setUser(user);
		saved = studentRepository.save(saved);

		// -------------------------------
		// GENERATE QR CODE (NON-BLOCKING)
		// -------------------------------
		try {
			studentQRCodeService.generateQRCodeForStudent(saved);
			log.info("QR code generated for student: {}", saved.getRollNumber());
		} catch (Exception e) {
			log.error("QR code generation failed for student: {}", saved.getRollNumber(), e);
		}

		log.info("Student registered successfully: {}", saved.getRollNumber());
		return saved;
	}

	// =====================================================
	// UPDATE STUDENT (COURSE OPTIONAL)
	// =====================================================
	@Transactional
	public Student updateStudent(Student student, MultipartFile photoFile) {

		Student existing = studentRepository.findById(student.getStudentId())
				.orElseThrow(() -> new RuntimeException(
						"Student not found with ID: " + student.getStudentId()));

		// Resolve Department (MANDATORY)
		Department resolvedDept = departmentRepository
				.findByDepartmentCode(student.getDepartment().getDepartmentCode())
				.orElseThrow(() -> new RuntimeException(
						"Invalid department code: " + student.getDepartment().getDepartmentCode()));

		// Resolve Course (OPTIONAL)
		Course resolvedCourse = null;
		if (student.getCourse() != null
				&& student.getCourse().getCourseCode() != null
				&& !student.getCourse().getCourseCode().isBlank()) {

			resolvedCourse = courseRepository
					.findByCourseCode(student.getCourse().getCourseCode())
					.orElseThrow(() -> new RuntimeException(
							"Invalid course code: " + student.getCourse().getCourseCode()));
		}

		student.setDepartment(resolvedDept);
		student.setCourse(resolvedCourse);

		// Preserve existing relations
		student.setUser(existing.getUser());
		student.setContact(existing.getContact());
		student.setCreatedAt(existing.getCreatedAt());

		// Photo update
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				String photoUrl = photoStorageService
						.storeStudentPhoto(student.getRollNumber(), photoFile.getBytes());
				student.setPhotoUrl(photoUrl);
			} catch (Exception e) {
				throw new RuntimeException("Failed to upload student photo", e);
			}
		} else {
			student.setPhotoUrl(existing.getPhotoUrl());
		}

		Student saved = studentRepository.save(student);
		log.info("Student updated successfully: {}", saved.getRollNumber());
		return saved;
	}

	// =====================================================
	// READ OPERATIONS
	// =====================================================
	public Student getStudentByRollNumber(String rollNumber) {
		if (rollNumber == null || rollNumber.isBlank()) {
			throw new IllegalArgumentException("Student roll number is required");
		}
		return studentRepository.findByRollNumber(rollNumber.trim())
				.orElseThrow(() -> new RuntimeException(
						"Student not found for roll number: " + rollNumber));
	}

	public Student getStudentById(Long id) {
		return studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
	}

	@Transactional(readOnly = true)
	public List<StudentProfileDTO> getAllStudentProfiles() {
		return studentRepository.findAll()
				.stream()
				.map(profileMapper::toProfileDTO)
				.collect(Collectors.toList());
	}

	// =====================================================
	// ROLL NUMBER GENERATION
	// =====================================================
	public String generateRollNumber(String departmentCode, Integer admissionYear) {
		if (departmentCode == null || departmentCode.isBlank()) {
			throw new IllegalArgumentException("Department code is required");
		}
		if (admissionYear == null || admissionYear < 1900) {
			throw new IllegalArgumentException("Valid admission year is required");
		}

		String yearSuffix = String.valueOf(admissionYear)
				.substring(String.valueOf(admissionYear).length() - 2);

		String prefix = departmentCode.toUpperCase() + "-" + yearSuffix + "-";
		int sequence = 1;
		String rollNumber;

		do {
			rollNumber = prefix + String.format("%03d", sequence++);
		} while (studentRepository.existsByRollNumber(rollNumber));

		return rollNumber;
	}

	// =====================================================
	// ADMISSION NUMBER GENERATION
	// =====================================================
	public String generateAdmissionNumber(Integer admissionYear) {
		if (admissionYear == null || admissionYear < 1900) {
			throw new IllegalArgumentException("Valid admission year is required");
		}

		String prefix = "ADM-" + admissionYear + "-";
		int sequence = 1;
		String admissionNumber;

		do {
			admissionNumber = prefix + String.format("%03d", sequence++);
		} while (studentRepository.existsByAdmissionNumber(admissionNumber));

		return admissionNumber;
	}
}
