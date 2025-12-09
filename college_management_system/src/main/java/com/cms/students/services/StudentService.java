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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

	private final StudentRepository studentRepository;
	private final DepartmentRepository departmentRepository;
	private final CourseRepository courseRepository;

	private final CommonUserService commonUserService;
	private final PhotoStorageService photoStorageService;

	private final StudentToProfileMapper profileMapper;
	private static final RoleEnum STUDENT_ROLE = RoleEnum.STUDENT;
	private static final String DEFAULT_PASSWORD = "stud@123";

	/**
	 * Register a new student (entity as input) Handles: ✔ department resolution
	 * using code ✔ course resolution using code ✔ student saving ✔ optional photo
	 * upload ✔ user creation
	 */
	@Transactional
	public Student registerStudent(Student student, MultipartFile photoFile) {

		// -----------------------------------------
		// 🔍 VALIDATIONS
		// -----------------------------------------

		// Roll number uniqueness
		if (studentRepository.existsByRollNumber(student.getRollNumber())) {
			throw new RuntimeException("Roll number already exists: " + student.getRollNumber());
		}

		// Admission number uniqueness
		if (studentRepository.existsByAdmissionNumber(student.getAdmissionNumber())) {
			throw new RuntimeException("Admission number already exists: " + student.getAdmissionNumber());
		}

		// -----------------------------------------
		// 🔄 FETCH department & course using codes
		// (Assuming student.getDepartment().deptCode is set)
		// -----------------------------------------

		Department resolvedDept = departmentRepository.findByDepartmentCode(student.getDepartment().getDepartmentCode())
				.orElseThrow(() -> new RuntimeException(
						"Invalid department code: " + student.getDepartment().getDepartmentCode()));

		Course resolvedCourse = courseRepository.findByCourseCode(student.getCourse().getCourseCode())
				.orElseThrow(() -> new RuntimeException("Invalid course code: " + student.getCourse().getCourseCode()));

		// assign actual entities
		student.setDepartment(resolvedDept);
		student.setCourse(resolvedCourse);

		Contact contact = student.getContact();

		// -----------------------------------------
		// 💾 SAVE STUDENT FIRST
		// -----------------------------------------
		Student saved = studentRepository.save(student);

		// -----------------------------------------
		// 📸 OPTIONAL PHOTO UPLOAD
		// -----------------------------------------
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				String photoUrl = photoStorageService.storeStudentPhoto(saved.getRollNumber(), photoFile.getBytes());

				saved.setPhotoUrl(photoUrl);
				saved = studentRepository.save(saved);

			} catch (Exception e) {
				throw new RuntimeException("Failed to upload student photo", e);
			}
		}

		// -----------------------------------------
		// 👤 CREATE USER ACCOUNT
		// -----------------------------------------
		var user = commonUserService.createUser(saved.getRollNumber(), // username
				STUDENT_ROLE, // role
				saved.getStudentId(), // referenceId
				DEFAULT_PASSWORD, // password
				contact // contact
		);

		if (user == null) {
			throw new RuntimeException("Failed to create user for student: " + saved.getRollNumber());
		}

		saved.setUser(user);
		saved = studentRepository.save(saved);

		log.info("Student registered with user + photo: {}", saved.getRollNumber());
		return saved;
	}

	public Student getStudentByRollNumber(String studentRoll) {
		if (studentRoll == null || studentRoll.trim().isEmpty()) {
			throw new IllegalArgumentException("Student roll number is required");
		}

		return studentRepository.findByRollNumber(studentRoll.trim())
				.orElseThrow(() -> new RuntimeException("Student not found for roll number: " + studentRoll));
	}

	public Student getStudentById(Long id) {
		return studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found for roll number: " + id));
	}

	@Transactional(readOnly = true)
	public List<StudentProfileDTO> getAllStudentProfiles() {
		try {
			List<Student> students = studentRepository.findAll(); // fetch all students
			return students.stream().map(profileMapper::toProfileDTO).collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Error fetching all student profiles", e);
			throw new RuntimeException("Failed to fetch student profiles", e);
		}
	}



}
