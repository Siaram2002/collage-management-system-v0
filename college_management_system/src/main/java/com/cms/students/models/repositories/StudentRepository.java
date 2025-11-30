package com.cms.students.models.repositories;

import com.cms.students.models.Student;
import com.cms.students.enums.StudentStatus;
import com.cms.students.enums.EnrollmentStatus;
import com.cms.college.models.Course;
import com.cms.college.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // -----------------------------------------------------
    // 🔍 BASIC FINDERS
    // -----------------------------------------------------

    /**
     * Find student by admission number (unique field).
     */
    Optional<Student> findByAdmissionNumber(String admissionNumber);

    /**
     * Find student by roll number (unique field).
     */
    Optional<Student> findByRollNumber(String rollNumber);

    /**
     * Check if admission number exists.
     */
    boolean existsByAdmissionNumber(String admissionNumber);

    /**
     * Check if roll number exists.
     */
    boolean existsByRollNumber(String rollNumber);


    // -----------------------------------------------------
    // 👤 USER / CONTACT LINKED
    // -----------------------------------------------------

    /**
     * Find student linked to a specific user account.
     */
    Optional<Student> findByUser_UserId(Long userId);

    /**
     * Find student linked to a contact entry.
     */
    Optional<Student> findByContact_ContactId(Long contactId);

    /**
     * Find student linked to a guardian contact entry.
     */
    Optional<Student> findByGuardianContact_ContactId(Long contactId);


    // -----------------------------------------------------
    // 🎓 DEPARTMENT / COURSE FILTERS
    // -----------------------------------------------------

    /**
     * Get all students in a department.
     */
    List<Student> findByDepartment(Department department);

    /**
     * Get all students enrolled in a course.
     */
    List<Student> findByCourse(Course course);


    // -----------------------------------------------------
    // 🚦 STATUS FILTERS
    // -----------------------------------------------------

    /**
     * Filter students by current status (ACTIVE / INACTIVE / SUSPENDED etc.).
     */
    List<Student> findByStatus(StudentStatus status);

    /**
     * Filter students by enrollment status (ENROLLED / GRADUATED / DROPPED etc.).
     */
    List<Student> findByEnrollmentStatus(EnrollmentStatus enrollmentStatus);


    // -----------------------------------------------------
    // 🔍 SEARCH HELPERS
    // -----------------------------------------------------

    /**
     * Search students by first, middle, or last name containing a keyword (case-insensitive).
     */
    List<Student> findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String middleName,
            String lastName
    );


    // -----------------------------------------------------
    // 📊 SORTED LISTING
    // -----------------------------------------------------

    /**
     * Get all students sorted by creation date (newest first).
     */
    List<Student> findAllByOrderByCreatedAtDesc();

    /**
     * Get all students sorted alphabetically by first name.
     */
    List<Student> findAllByOrderByFirstNameAsc();
}
