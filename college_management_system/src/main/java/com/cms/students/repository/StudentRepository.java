package com.cms.students.repository;

import com.cms.busPass.StudentBusPass;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.models.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    // ------------------- UNIQUE FINDERS -------------------
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByAdmissionNumber(String admissionNumber);

    boolean existsByRollNumber(String rollNumber);
    boolean existsByAdmissionNumber(String admissionNumber);

    // ------------------- PAGINATED FILTERS -------------------
    Page<Student> findByDepartment_DepartmentId(Long departmentId, Pageable pageable);
    Page<Student> findByCourse_CourseId(Long courseId, Pageable pageable);
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
    Page<Student> findByEnrollmentStatus(EnrollmentStatus status, Pageable pageable);

    Page<Student> findByDepartment_DepartmentIdAndCourse_CourseIdAndStatus(
            Long departmentId,
            Long courseId,
            StudentStatus status,
            Pageable pageable
    );

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

    // ------------------- DYNAMIC SEARCH + KEYWORD -------------------
    @Query("""
        SELECT s FROM Student s
        WHERE 
            (:departmentId IS NULL OR s.department.departmentId = :departmentId)
        AND (:courseId IS NULL OR s.course.courseId = :courseId)
        AND (:status IS NULL OR s.status = :status)
        AND (
               :keyword IS NULL 
               OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Student> searchStudents(
            @Param("departmentId") Long departmentId,
            @Param("courseId") Long courseId,
            @Param("status") StudentStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // ------------------- BULK OPTIMIZATION -------------------
    // Fetch all roll numbers only → useful for CSV bulk upload
    @Query("SELECT s.rollNumber FROM Student s")
    List<String> findAllRollNumbers();

    // Optionally, fetch by admission numbers for bulk checks
    @Query("SELECT s.admissionNumber FROM Student s")
    List<String> findAllAdmissionNumbers();
	Optional<Student> findByRollNumberAndAdmissionNumber(String rollNumber, String admissionNumber);
    
    
    
    

    
    
}
