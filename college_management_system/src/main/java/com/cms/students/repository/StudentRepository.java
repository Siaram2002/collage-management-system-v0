package com.cms.students.repository;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.models.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find by unique fields
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByAdmissionNumber(String admissionNumber);

    boolean existsByRollNumber(String rollNumber);
    boolean existsByAdmissionNumber(String admissionNumber);

    // Filters with pagination
    Page<Student> findByDepartment_DepartmentId(Long departmentId, Pageable pageable);

    Page<Student> findByCourse_CourseId(Long courseId, Pageable pageable);

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    Page<Student> findByEnrollmentStatus(EnrollmentStatus status, Pageable pageable);

    // Combined filters
    Page<Student> findByDepartment_DepartmentIdAndCourse_CourseIdAndStatus(
            Long departmentId,
            Long courseId,
            StudentStatus status,
            Pageable pageable
    );

    // Optionally: filter by multiple fields dynamically (Spring Data JPA query methods)
    Page<Student> findByDepartment_DepartmentIdAndCourse_CourseId(
            Long departmentId,
            Long courseId,
            Pageable pageable
    );

    Page<Student> findByDepartment_DepartmentIdAndStatus(
            Long departmentId,
            StudentStatus status,
            Pageable pageable
    );

    Page<Student> findByCourse_CourseIdAndStatus(
            Long courseId,
            StudentStatus status,
            Pageable pageable
    );
}
